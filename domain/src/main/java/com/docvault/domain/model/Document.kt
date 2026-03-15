package com.docvault.domain.model

data class Document(
    val id: String,
    val name: String,
    val type: DocumentType,
    val filePath: String,
    val createdAt: Long,
    val fileSize: Long,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
)
