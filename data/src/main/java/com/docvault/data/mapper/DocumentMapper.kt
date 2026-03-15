package com.docvault.data.mapper

import com.docvault.data.database.entity.DocumentEntity
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType

fun DocumentEntity.toDomain(): Document =
    Document(
        id = id,
        name = name,
        type = DocumentType.fromString(type),
        filePath = encryptedFilePath,
        createdAt = createdAt,
        fileSize = fileSize,
        latitude = latitude,
        longitude = longitude,
        address = address,
    )

fun Document.toEntity(): DocumentEntity =
    DocumentEntity(
        id = id,
        name = name,
        type = type.name,
        encryptedFilePath = filePath,
        createdAt = createdAt,
        fileSize = fileSize,
        latitude = latitude,
        longitude = longitude,
        address = address,
    )
