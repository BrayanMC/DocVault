package com.docvault.lib.security.di

import com.docvault.lib.security.biometric.BiometricAuthManager
import com.docvault.lib.security.biometric.BiometricAuthManagerImpl
import com.docvault.lib.security.crypto.CryptoManager
import com.docvault.lib.security.crypto.CryptoManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds security interfaces to their implementations.
 * Installed in [SingletonComponent] — both managers are stateless and safe to share.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {
    @Binds
    @Singleton
    abstract fun bindCryptoManager(impl: CryptoManagerImpl): CryptoManager

    @Binds
    @Singleton
    abstract fun bindBiometricAuthManager(impl: BiometricAuthManagerImpl): BiometricAuthManager
}
