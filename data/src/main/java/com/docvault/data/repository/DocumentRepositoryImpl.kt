package com.docvault.data.repository

import com.docvault.core.common.extensions.asResult
import com.docvault.core.common.extensions.safeCall
import com.docvault.core.common.result.DocVaultResult
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.filesystem.SecureFileManager
import com.docvault.data.mapper.toDomain
import com.docvault.data.mapper.toEntity
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val secureFileManager: SecureFileManager,
) : DocumentRepository {
    override fun getAllDocuments(): Flow<DocVaultResult<List<Document>>> =
        documentDao.getAllDocuments()
            .map { entities -> entities.map { it.toDomain() } }
            .asResult()

    override fun getDocumentsByType(type: DocumentType): Flow<DocVaultResult<List<Document>>> =
        documentDao.getDocumentsByType(type.name)
            .map { entities -> entities.map { it.toDomain() } }
            .asResult()

    override suspend fun getDocumentById(id: String): DocVaultResult<Document> =
        safeCall {
            documentDao.getDocumentById(id)?.toDomain()
                ?: throw NoSuchElementException("Document not found: $id")
        }

    override suspend fun addDocument(
        document: Document,
        fileBytes: ByteArray,
    ): DocVaultResult<Unit> =
        safeCall {
            val filePath = secureFileManager.saveFile(document.id, fileBytes)
            documentDao.insertDocument(document.copy(filePath = filePath).toEntity())
        }

    override suspend fun deleteDocument(document: Document): DocVaultResult<Unit> =
        safeCall {
            secureFileManager.deleteFile(document.filePath)
            documentDao.deleteDocument(document.toEntity())
        }
}
