package com.docvault.feature.detail

import app.cash.turbine.test
import com.docvault.core.common.result.DocVaultResult
import com.docvault.data.filesystem.SecureFileManager
import com.docvault.data.watermark.WatermarkManager
import com.docvault.domain.model.AccessLog
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.usecase.DeleteDocumentUseCase
import com.docvault.domain.usecase.GetAccessLogsUseCase
import com.docvault.domain.usecase.GetDocumentDetailUseCase
import com.docvault.domain.usecase.RegisterAccessLogUseCase
import com.docvault.feature.detail.presentation.state.DetailUiState
import com.docvault.feature.detail.presentation.viewmodel.DetailViewModel
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getDocumentDetailUseCase: GetDocumentDetailUseCase = mockk()
    private val deleteDocumentUseCase: DeleteDocumentUseCase = mockk()
    private val getAccessLogsUseCase: GetAccessLogsUseCase = mockk()
    private val registerAccessLogUseCase: RegisterAccessLogUseCase = mockk()
    private val watermarkManager: WatermarkManager = mockk()
    private val secureFileManager: SecureFileManager = mockk()

    private lateinit var viewModel: DetailViewModel

    private val fakeDocument = Document(
        id = "1",
        name = "test.jpg",
        type = DocumentType.IMAGE,
        filePath = "/path/test.jpg",
        createdAt = 1000L,
        fileSize = 1024L,
        latitude = -12.0,
        longitude = -77.0,
        address = "Av. Test 123"
    )

    private val fakeAccessLogs = listOf(
        AccessLog(id = 1, documentId = "1", accessedAt = 1000L),
        AccessLog(id = 2, documentId = "1", accessedAt = 2000L)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DetailViewModel(
            getDocumentDetailUseCase,
            deleteDocumentUseCase,
            getAccessLogsUseCase,
            registerAccessLogUseCase,
            watermarkManager,
            secureFileManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDocument emits Success state with document and bytes`() = runTest {
        coEvery { getDocumentDetailUseCase("1") } returns DocVaultResult.Success(fakeDocument)
        coEvery { registerAccessLogUseCase("1") } returns DocVaultResult.Success(Unit)
        coEvery { secureFileManager.readFile(fakeDocument.filePath) } returns byteArrayOf(1, 2, 3)
        every { getAccessLogsUseCase("1") } returns flowOf(
            DocVaultResult.Success(fakeAccessLogs)
        )

        viewModel.loadDocument("1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DetailUiState.Success)
            with(state) {
                assertEquals(fakeDocument, document)
                assertTrue(decryptedBytes != null)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadDocument emits error when getDocumentDetail fails`() = runTest {
        coEvery { getDocumentDetailUseCase("1") } returns DocVaultResult.Error(
            Exception("Documento no encontrado")
        )

        val errors = mutableListOf<String>()
        val job = launch { viewModel.error.collect { errors.add(it) } }

        viewModel.loadDocument("1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Documento no encontrado", errors.first())
        job.cancel()
    }

    @Test
    fun `loadDocument emits Success with null bytes when readFile fails`() = runTest {
        coEvery { getDocumentDetailUseCase("1") } returns DocVaultResult.Success(fakeDocument)
        coEvery { registerAccessLogUseCase("1") } returns DocVaultResult.Success(Unit)
        coEvery { secureFileManager.readFile(any()) } throws Exception("Error al leer")
        every { getAccessLogsUseCase("1") } returns flowOf(DocVaultResult.Success(emptyList()))

        viewModel.loadDocument("1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DetailUiState.Success)
            assertNull(state.decryptedBytes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadDocument updates accessLogs after loading`() = runTest {
        coEvery { getDocumentDetailUseCase("1") } returns DocVaultResult.Success(fakeDocument)
        coEvery { registerAccessLogUseCase("1") } returns DocVaultResult.Success(Unit)
        coEvery { secureFileManager.readFile(any()) } returns byteArrayOf(1, 2, 3)
        every { getAccessLogsUseCase("1") } returns flowOf(
            DocVaultResult.Success(fakeAccessLogs)
        )

        viewModel.loadDocument("1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DetailUiState.Success)
            assertEquals(2, state.accessLogs.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteDocument emits Deleted state on success`() = runTest {
        coEvery { deleteDocumentUseCase(fakeDocument) } returns DocVaultResult.Success(Unit)

        viewModel.deleteDocument(fakeDocument)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DetailUiState.Deleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteDocument emits error on failure`() = runTest {
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