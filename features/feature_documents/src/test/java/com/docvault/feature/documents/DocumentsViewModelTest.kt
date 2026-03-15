package com.docvault.feature.documents

import android.net.Uri
import app.cash.turbine.test
import com.docvault.core.common.result.DocVaultResult
import com.docvault.data.filesystem.DocumentFileManager
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.usecase.AddDocumentUseCase
import com.docvault.domain.usecase.DeleteDocumentUseCase
import com.docvault.domain.usecase.GetDocumentsUseCase
import com.docvault.feature.documents.presentation.state.DocumentsUiState
import com.docvault.feature.documents.presentation.viewmodel.DocumentsViewModel
import com.docvault.lib.location.DocVaultLocationManager
import com.docvault.lib.location.model.LocationResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class DocumentsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getDocumentsUseCase: GetDocumentsUseCase = mockk()
    private val addDocumentUseCase: AddDocumentUseCase = mockk()
    private val deleteDocumentUseCase: DeleteDocumentUseCase = mockk()
    private val locationManager: DocVaultLocationManager = mockk()
    private val documentFileManager: DocumentFileManager = mockk()

    private lateinit var viewModel: DocumentsViewModel

    private val fakeDocument = Document(
        id = "1",
        name = "test.jpg",
        type = DocumentType.IMAGE,
        filePath = "/path/test.jpg",
        createdAt = 1000L,
        fileSize = 1024L,
        latitude = null,
        longitude = null,
        address = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getDocumentsUseCase(null) } returns flowOf(
            DocVaultResult.Success(listOf(fakeDocument))
        )
        viewModel = DocumentsViewModel(
            getDocumentsUseCase,
            addDocumentUseCase,
            deleteDocumentUseCase,
            locationManager,
            documentFileManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDocuments emits Success state with documents`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DocumentsUiState.Success)
            assertEquals(1, (state as DocumentsUiState.Success).documents.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadDocuments emits Error state on failure`() = runTest {
        every { getDocumentsUseCase(null) } returns flowOf(
            DocVaultResult.Error(Exception("Error de red"))
        )
        viewModel.loadDocuments()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DocumentsUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filterByType PDF emits Success with filtered documents`() = runTest {
        every { getDocumentsUseCase(DocumentType.PDF) } returns flowOf(
            DocVaultResult.Success(emptyList())
        )
        viewModel.filterByType(DocumentType.PDF)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DocumentsUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addDocument emits Error when readBytes returns null`() = runTest {
        val uri = mockk<Uri>()
        every { documentFileManager.getMimeType(uri) } returns "image/jpeg"
        every { documentFileManager.getFileName(uri) } returns "test.jpg"
        every { documentFileManager.readBytes(uri) } returns null

        viewModel.addDocument(uri)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DocumentsUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addDocument success reloads documents`() = runTest {
        val uri = mockk<Uri>()
        val bytes = byteArrayOf(1, 2, 3)
        every { documentFileManager.getMimeType(uri) } returns "image/jpeg"
        every { documentFileManager.getFileName(uri) } returns "test.jpg"
        every { documentFileManager.readBytes(uri) } returns bytes
        coEvery { locationManager.getCurrentLocation() } returns LocationResult(0.0, 0.0)
        coEvery {
            locationManager.getAddressFromCoordinates(any(), any())
        } returns "Calle Test 123"
        coEvery { addDocumentUseCase(any(), any()) } returns DocVaultResult.Success(Unit)
        every { getDocumentsUseCase(null) } returns flowOf(
            DocVaultResult.Success(listOf(fakeDocument))
        )

        viewModel.addDocument(uri)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DocumentsUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteDocument success reloads documents`() = runTest {
        coEvery { deleteDocumentUseCase(fakeDocument) } returns DocVaultResult.Success(Unit)

        viewModel.deleteDocument(fakeDocument)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DocumentsUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteDocument error emits error message`() = runTest {
        coEvery { deleteDocumentUseCase(fakeDocument) } returns DocVaultResult.Error(
            Exception("Error al eliminar")
        )

        val errors = mutableListOf<String>()
        val job = launch { viewModel.error.collect { errors.add(it) } }

        viewModel.deleteDocument(fakeDocument)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Error al eliminar", errors.first())
        job.cancel()
    }
}