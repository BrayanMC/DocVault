package com.docvault.lib.camera.di

import com.docvault.lib.camera.CameraManager
import com.docvault.lib.camera.CameraManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CameraModule {
    @Binds
    @Singleton
    abstract fun bindCameraManager(impl: CameraManagerImpl): CameraManager
}
