package com.docvault.lib.location.di

import com.docvault.lib.location.DocVaultLocationManager
import com.docvault.lib.location.DocVaultLocationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    @Singleton
    abstract fun bindLocationManager(impl: DocVaultLocationManagerImpl): DocVaultLocationManager
}
