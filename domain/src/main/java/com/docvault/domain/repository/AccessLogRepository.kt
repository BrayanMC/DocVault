package com.docvault.domain.repository

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.AccessLog
import kotlinx.coroutines.flow.Flow

/**
 * Contract for access log persistence operations.
 * Implemented in the data layer, consumed by use cases in the domain layer.
 */
interface AccessLogRepository {
    fun getAccessLogs(documentId: String): Flow<DocVaultResult<List<AccessLog>>>

    suspend fun registerAccess(documentId: String): DocVaultResult<Unit>

    suspend fun deleteAccessLogs(documentId: String): DocVaultResult<Unit>
}
