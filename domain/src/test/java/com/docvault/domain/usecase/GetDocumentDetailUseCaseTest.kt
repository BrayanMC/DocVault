package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDocumentDetailUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private lateinit var useCase: GetDocumentDetailUseCase

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
        useCase = GetDocumentDetailUseCase(repository)
    }

    @Test
    fun `invoke calls repository and returns Success with document`() = runTest {
        coEvery { repository.getDocumentById("1") } returns DocVaultResult.Success(fakeDocument)

        val result = useCase("1")

        assertTrue(result is DocVaultResult.Success)
        assertEquals(fakeDocument, (result as DocVaultResult.Success).data)
        coVerify { repository.getDocumentById("1") }
    }

    @Test
    fun `invoke returns Error when document not found`() = runTest {
        coEvery { repository.getDocumentById("1") } returns DocVaultResult.Error(
            NoSuchElementException("Document not found: 1")
        )

        val result = useCase("1")

        assertTrue(result is DocVaultResult.Error)
        assertEquals("Document not found: 1", (result as DocVaultResult.Error).throwable.message)
    }
}