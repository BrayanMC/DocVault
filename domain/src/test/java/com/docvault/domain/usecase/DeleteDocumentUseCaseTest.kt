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

class DeleteDocumentUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private lateinit var useCase: DeleteDocumentUseCase

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
        useCase = DeleteDocumentUseCase(repository)
    }

    @Test
    fun `invoke calls repository deleteDocument`() = runTest {
        coEvery { repository.deleteDocument(fakeDocument) } returns DocVaultResult.Success(Unit)

        useCase(fakeDocument)

        coVerify { repository.deleteDocument(fakeDocument) }
    }

    @Test
    fun `invoke returns Success when repository succeeds`() = runTest {
        coEvery { repository.deleteDocument(fakeDocument) } returns DocVaultResult.Success(Unit)

        val result = useCase(fakeDocument)

        assertTrue(result is DocVaultResult.Success)
    }

    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        coEvery { repository.deleteDocument(fakeDocument) } returns DocVaultResult.Error(
            Exception("Error al eliminar")
        )

        val result = useCase(fakeDocument)

        assertTrue(result is DocVaultResult.Error)
    }
}