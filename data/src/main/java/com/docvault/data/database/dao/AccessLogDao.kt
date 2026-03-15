package com.docvault.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.docvault.data.database.entity.AccessLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessLogDao {
    @Query("SELECT * FROM access_logs WHERE documentId = :documentId ORDER BY accessedAt DESC LIMIT 10")
    fun getAccessLogs(documentId: String): Flow<List<AccessLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessLog(accessLog: AccessLogEntity)

    @Query("DELETE FROM access_logs WHERE documentId = :documentId")
    suspend fun deleteAccessLogsByDocument(documentId: String)
}
