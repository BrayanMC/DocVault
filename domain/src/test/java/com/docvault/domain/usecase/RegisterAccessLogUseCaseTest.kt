package com.docvault.domain.usecase

import com.docvault.core.common.result.DocVaultResult
import com.docvault.domain.repository.AccessLogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterAccessLogUseCaseTest {

    private val repository: AccessLogRepository = mockk()
    private lateinit var useCase: RegisterAccessLogUseCase

    @Before
    fun setup() {
        useCase = RegisterAccessLogUseCase(repository)
    }

    @Test
    fun `invoke calls repository registerAccess`() = runTest {
        coEvery { repository.registerAccess("1") } returns DocVaultResult.Success(Unit)

        useCase("1")

        coVerify { repository.registerAccess("1") }
    }

    @Test
    fun `invoke returns Success when registration succeeds`() = runTest {
        coEvery { repository.registerAccess("1") } returns DocVaultResult.Success(Unit)

        val result = useCase("1")

        assertTrue(result is DocVaultResult.Success)
    }

    @Test
    fun `invoke returns Error when registration fails`() = runTest {
        coEvery { repository.registerAccess("1") } returns DocVaultResult.Error(
            Exception("Error al registrar acceso")
        )

        val result = useCase("1")

        assertTrue(result is DocVaultResult.Error)
    }
}