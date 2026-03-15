package com.docvault.data.mapper

import com.docvault.data.database.entity.AccessLogEntity
import com.docvault.domain.model.AccessLog

fun AccessLogEntity.toDomain(): AccessLog =
    AccessLog(
        id = id,
        documentId = documentId,
        accessedAt = accessedAt,
    )

fun AccessLog.toEntity(): AccessLogEntity =
    AccessLogEntity(
        id = id,
        documentId = documentId,
        accessedAt = accessedAt,
    )
