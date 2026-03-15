package com.docvault.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.entity.AccessLogEntity
import com.docvault.data.database.entity.DocumentEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessLogDaoTest {

    private lateinit var database: DocVaultDatabase
    private lateinit var dao: AccessLogDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DocVaultDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.accessLogDao()

        runBlocking {
            database.documentDao().insertDocument(
                DocumentEntity(
                    id = "1",
                    name = "test.pdf",
                    type = "PDF",
                    encryptedFilePath = "/path/test.pdf",
                    createdAt = 1000L,
                    fileSize = 2048L,
                    latitude = null,
                    longitude = null,
                    address = null
                )
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAccessLogs() = runTest {
        val log = AccessLogEntity(id = 0, documentId = "1", accessedAt = 1000L)
        dao.insertAccessLog(log)

        val logs = dao.getAccessLogs("1").first()
        assertEquals(1, logs.size)
        assertEquals("1", logs.first().documentId)
    }

    @Test
    fun getAccessLogs_returnsEmpty_whenNoneExist() = runTest {
        val logs = dao.getAccessLogs("nonexistent").first()
        assertEquals(0, logs.size)
    }

    @Test
    fun insertMultipleLogs_returnsAllForDocument() = runTest {
        val doc2 = DocumentEntity(
            id = "2",
            name = "test2.pdf",
            type = "PDF",
            encryptedFilePath = "/path/test2.pdf",
            createdAt = 1000L,
            fileSize = 1024L,
            latitude = null,
            longitude = null,
            address = null
        )
        runBlocking {
            database.documentDao().insertDocument(doc2)
        }

        dao.insertAccessLog(AccessLogEntity(id = 0, documentId = "1", accessedAt = 1000L))
        dao.insertAccessLog(AccessLogEntity(id = 0, documentId = "1", accessedAt = 2000L))
        dao.insertAccessLog(AccessLogEntity(id = 0, documentId = "2", accessedAt = 3000L))

        val logs = dao.getAccessLogs("1").first()
        assertEquals(2, logs.size)
    }
}