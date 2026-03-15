package com.docvault.data.repository

import com.docvault.core.common.extensions.asResult
import com.docvault.core.common.extensions.safeCall
import com.docvault.core.common.result.DocVaultResult
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.entity.AccessLogEntity
import com.docvault.data.mapper.toDomain
import com.docvault.domain.model.AccessLog
import com.docvault.domain.repository.AccessLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccessLogRepositoryImpl @Inject constructor(
    private val accessLogDao: AccessLogDao,
) : AccessLogRepository {
    override fun getAccessLogs(documentId: String): Flow<DocVaultResult<List<AccessLog>>> =
        accessLogDao.getAccessLogs(documentId)
            .map { entities -> entities.map { it.toDomain() } }
            .asResult()

    override suspend fun registerAccess(documentId: String): DocVaultResult<Unit> =
        safeCall {
            accessLogDao.insertAccessLog(
                AccessLogEntity(
                    documentId = documentId,
                    accessedAt = System.currentTimeMillis(),
                ),
            )
        }

    override suspend fun deleteAccessLogs(documentId: String): DocVaultResult<Unit> =
        safeCall {
            accessLogDao.deleteAccessLogsByDocument(documentId)
        }
}
