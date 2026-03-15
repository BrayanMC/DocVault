package com.docvault.domain.usecase

import app.cash.turbine.test
import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.repository.DocumentRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDocumentsUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private lateinit var useCase: GetDocumentsUseCase

    private val fakeDocuments = listOf(
        Document(
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
    )

    @Before
    fun setup() {
        useCase = GetDocumentsUseCase(repository)
    }

    @Test
    fun `invoke with null type returns Success with documents`() = runTest {
        every { repository.getAllDocuments() } returns flowOf(
            DocVaultResult.Success(fakeDocuments)
        )

        useCase(null).test {
            val result = awaitItem()
            assertTrue(result is DocVaultResult.Success)
            assertEquals(fakeDocuments, (result as DocVaultResult.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with PDF type returns Success with empty list`() = runTest {
        every { repository.getDocumentsByType(DocumentType.PDF) } returns flowOf(
            DocVaultResult.Success(emptyList())
        )

        useCase(DocumentType.PDF).test {
            val result = awaitItem()
            assertTrue(result is DocVaultResult.Success)
            assertTrue((result as DocVaultResult.Success).data.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns documents from repository`() = runTest {
        every { repository.getAllDocuments() } returns flowOf(
            DocVaultResult.Success(fakeDocuments)
        )

        useCase(null).test {
            val result = awaitItem()
            assertEquals(DocVaultResult.Success(fakeDocuments), result)
            awaitComplete()
        }
    }
}