package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.repository.AccessLogRepository
import javax.inject.Inject

class RegisterAccessLogUseCase @Inject constructor(
    private val repository: AccessLogRepository,
) {
    suspend operator fun invoke(documentId: String): DocVaultResult<Unit> =
        repository.registerAccess(documentId)
}
