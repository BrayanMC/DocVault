package com.docvault.data.repository

import com.docvault.core.common.result.DocVaultResult
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.entity.AccessLogEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AccessLogRepositoryImplTest {

    private val accessLogDao: AccessLogDao = mockk()
    private lateinit var repository: AccessLogRepositoryImpl

    private val fakeEntities = listOf(
        AccessLogEntity(id = 1, documentId = "1", accessedAt = 1000L),
        AccessLogEntity(id = 2, documentId = "1", accessedAt = 2000L)
    )

    @Before
    fun setup() {
        repository = AccessLogRepositoryImpl(accessLogDao)
    }

    @Test
    fun `getAccessLogs returns mapped logs from DAO`() = runTest {
        every { accessLogDao.getAccessLogs("1") } returns flowOf(fakeEntities)

        repository.getAccessLogs("1")
            .filter { it is DocVaultResult.Success }
            .collect { result ->
                assertEquals(2, (result as DocVaultResult.Success).data.size)
            }
    }

    @Test
    fun `getAccessLogs returns empty list when no logs exist`() = runTest {
        every { accessLogDao.getAccessLogs("1") } returns flowOf(emptyList())

        repository.getAccessLogs("1")
            .filter { it is DocVaultResult.Success }
            .collect { result ->
                assertEquals(0, (result as DocVaultResult.Success).data.size)
            }
    }

    @Test
    fun `registerAccess calls dao insertAccessLog`() = runTest {
        coEvery { accessLogDao.insertAccessLog(any()) } returns Unit

        val result = repository.registerAccess("1")

        assertTrue(result is DocVaultResult.Success)
        coVerify { accessLogDao.insertAccessLog(any()) }
    }

    @Test
    fun `registerAccess returns Error when dao throws`() = runTest {
        coEvery { accessLogDao.insertAccessLog(any()) } throws Exception("DB error")

        val result = repository.registerAccess("1")

        assertTrue(result is DocVaultResult.Error)
    }
}