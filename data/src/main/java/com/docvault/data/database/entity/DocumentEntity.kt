package com.docvault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val encryptedFilePath: String,
    val createdAt: Long,
    val fileSize: Long,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
)
