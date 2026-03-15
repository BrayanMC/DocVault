package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDocumentsUseCase @Inject constructor(
    private val repository: DocumentRepository,
) {
    operator fun invoke(type: DocumentType? = null): Flow<DocVaultResult<List<Document>>> =
        if (type != null) {
            repository.getDocumentsByType(type)
        } else {
            repository.getAllDocuments()
        }
}
