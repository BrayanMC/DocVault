package com.docvault.data.di

import android.content.Context
import androidx.room.Room
import com.docvault.data.database.DocVaultDatabase
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.dao.DocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): DocVaultDatabase =
        Room.databaseBuilder(
            context,
            DocVaultDatabase::class.java,
            "docvault_database",
        ).build()

    @Provides
    fun provideDocumentDao(database: DocVaultDatabase): DocumentDao =
        database.documentDao()

    @Provides
    fun provideAccessLogDao(database: DocVaultDatabase): AccessLogDao =
        database.accessLogDao()
}