package com.docvault.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.database.entity.AccessLogEntity
import com.docvault.data.database.entity.DocumentEntity

@Database(
    entities = [DocumentEntity::class, AccessLogEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class DocVaultDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    abstract fun accessLogDao(): AccessLogDao
}
