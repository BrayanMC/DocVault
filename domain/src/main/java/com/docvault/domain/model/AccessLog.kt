package com.docvault.domain.model

data class AccessLog(
    val id: Long = 0,
    val documentId: String,
    val accessedAt: Long,
)
