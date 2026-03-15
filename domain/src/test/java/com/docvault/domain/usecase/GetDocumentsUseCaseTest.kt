package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.repository.DocumentRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    fun `invoke with null type calls getAllDocuments`() = runTest {
        every { repository.getAllDocuments() } returns flowOf(
            DocVaultResult.Success(fakeDocuments)
        )

        useCase(null)

        verify { repository.getAllDocuments() }
    }

    @Test
    fun `invoke with PDF type calls getDocumentsByType`() = runTest {
        every { repository.getDocumentsByType(DocumentType.PDF) } returns flowOf(
            DocVaultResult.Success(emptyList())
        )

        useCase(DocumentType.PDF)

        verify { repository.getDocumentsByType(DocumentType.PDF) }
    }

    @Test
    fun `invoke returns documents from repository`() = runTest {
        every { repository.getAllDocuments() } returns flowOf(
            DocVaultResult.Success(fakeDocuments)
        )

        val flow = useCase(null)
        flow.collect { result ->
            assertEquals(DocVaultResult.Success(fakeDocuments), result)
        }
    }
}