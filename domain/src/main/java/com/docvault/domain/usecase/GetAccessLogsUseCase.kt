package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.AccessLog
import com.docvault.domain.repository.AccessLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccessLogsUseCase @Inject constructor(
    private val repository: AccessLogRepository,
) {
    operator fun invoke(documentId: String): Flow<DocVaultResult<List<AccessLog>>> =
        repository.getAccessLogs(documentId)
}
