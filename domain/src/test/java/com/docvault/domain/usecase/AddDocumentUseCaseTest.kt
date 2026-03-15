package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddDocumentUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private lateinit var useCase: AddDocumentUseCase

    private val fakeDocument = Document(
        id = "1",
        name = "test.jpg",
        type = DocumentType.IMAGE,
        filePath = "",
        createdAt = 1000L,
        fileSize = 1024L,
        latitude = null,
        longitude = null,
        address = null
    )

    @Before
    fun setup() {
        useCase = AddDocumentUseCase(repository)
    }

    @Test
    fun `invoke calls repository addDocument`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { repository.addDocument(fakeDocument, bytes) } returns DocVaultResult.Success(Unit)

        useCase(fakeDocument, bytes)

        coVerify { repository.addDocument(fakeDocument, bytes) }
    }

    @Test
    fun `invoke returns Success when repository succeeds`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { repository.addDocument(fakeDocument, bytes) } returns DocVaultResult.Success(Unit)

        val result = useCase(fakeDocument, bytes)

        assertTrue(result is DocVaultResult.Success)
    }

    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { repository.addDocument(fakeDocument, bytes) } returns DocVaultResult.Error(
            Exception("Error al guardar")
        )

        val result = useCase(fakeDocument, bytes)

        assertTrue(result is DocVaultResult.Error)
    }
}