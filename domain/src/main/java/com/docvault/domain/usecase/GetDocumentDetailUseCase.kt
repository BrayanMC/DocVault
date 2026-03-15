package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.Document
import com.docvault.domain.repository.DocumentRepository
import javax.inject.Inject

class GetDocumentDetailUseCase @Inject constructor(
    private val repository: DocumentRepository,
) {
    suspend operator fun invoke(documentId: String): DocVaultResult<Document> =
        repository.getDocumentById(documentId)
}
