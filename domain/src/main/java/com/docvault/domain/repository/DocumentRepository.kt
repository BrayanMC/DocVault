package com.docvault.domain.repository

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import kotlinx.coroutines.flow.Flow

/**
 * Contract for document persistence operations.
 * Implemented in the data layer, consumed by use cases in the domain layer.
 */
interface DocumentRepository {
    fun getAllDocuments(): Flow<DocVaultResult<List<Document>>>

    fun getDocumentsByType(type: DocumentType): Flow<DocVaultResult<List<Document>>>

    suspend fun getDocumentById(id: String): DocVaultResult<Document>

    suspend fun addDocument(
        document: Document,
        fileBytes: ByteArray,
    ): DocVaultResult<Unit>

    suspend fun deleteDocument(document: Document): DocVaultResult<Unit>
}
