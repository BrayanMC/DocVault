package com.docvault.data.di

import android.content.Context
import androidx.room.Room
import com.docvault.data.database.DocVaultDatabase
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.filesystem.SecureFileManager
import com.docvault.data.filesystem.SecureFileManagerImpl
import com.docvault.data.repository.AccessLogRepositoryImpl
import com.docvault.data.repository.DocumentRepositoryImpl
import com.docvault.data.watermark.WatermarkManager
import com.docvault.data.watermark.WatermarkManagerImpl
import com.docvault.domain.repository.AccessLogRepository
import com.docvault.domain.repository.DocumentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindAccessLogRepository(impl: AccessLogRepositoryImpl): AccessLogRepository

    @Binds
    @Singleton
    abstract fun bindSecureFileManager(impl: SecureFileManagerImpl): SecureFileManager

    @Binds
    @Singleton
    abstract fun bindWatermarkManager(impl: WatermarkManagerImpl): WatermarkManager
}
