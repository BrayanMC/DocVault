package com.docvault.domain.usecase

import app.cash.turbine.test
import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.model.AccessLog
import com.docvault.domain.repository.AccessLogRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetAccessLogsUseCaseTest {

    private val repository: AccessLogRepository = mockk()
    private lateinit var useCase: GetAccessLogsUseCase

    private val fakeAccessLogs = listOf(
        AccessLog(id = 1, documentId = "1", accessedAt = 1000L),
        AccessLog(id = 2, documentId = "1", accessedAt = 2000L)
    )

    @Before
    fun setup() {
        useCase = GetAccessLogsUseCase(repository)
    }

    @Test
    fun `invoke returns access logs from repository`() = runTest {
        every { repository.getAccessLogs("1") } returns flowOf(
            DocVaultResult.Success(fakeAccessLogs)
        )

        useCase("1").test {
            val result = awaitItem()
            assertTrue(result is DocVaultResult.Success)
            assertEquals(fakeAccessLogs, (result as DocVaultResult.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when no logs exist`() = runTest {
        every { repository.getAccessLogs("1") } returns flowOf(
            DocVaultResult.Success(emptyList())
        )

        useCase("1").test {
            val result = awaitItem()
            assertTrue(result is DocVaultResult.Success)
            assertTrue((result as DocVaultResult.Success).data.isEmpty())
            awaitComplete()
        }
    }
}