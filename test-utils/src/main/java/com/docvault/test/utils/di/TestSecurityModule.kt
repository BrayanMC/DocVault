package com.docvault.test.utils.di

import com.docvault.lib.security.biometric.BiometricAuthManager
import com.docvault.lib.security.crypto.CryptoManager
import com.docvault.lib.security.crypto.CryptoManagerImpl
import com.docvault.lib.security.di.SecurityModule
import com.docvault.test.utils.fake.FakeBiometricAuthManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SecurityModule::class]
)
abstract class TestSecurityModule {

    @Binds
    @Singleton
    abstract fun bindCryptoManager(impl: CryptoManagerImpl): CryptoManager

    companion object {
        @Provides
        @Singleton
        fun provideFakeBiometricAuthManager(): BiometricAuthManager =
            FakeBiometricAuthManager()
    }
}