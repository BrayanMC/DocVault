package com.docvault.test.utils.di

import android.content.Context
import androidx.room.Room
import com.docvault.data.database.DocVaultDatabase
import com.docvault.data.database.dao.AccessLogDao
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(
        @ApplicationContext context: Context,
    ): DocVaultDatabase =
        Room.inMemoryDatabaseBuilder(
            context,
            DocVaultDatabase::class.java,
        ).allowMainThreadQueries().build()

    @Provides
    fun provideDocumentDao(database: DocVaultDatabase): DocumentDao =
        database.documentDao()

    @Provides
    fun provideAccessLogDao(database: DocVaultDatabase): AccessLogDao =
        database.accessLogDao()
}