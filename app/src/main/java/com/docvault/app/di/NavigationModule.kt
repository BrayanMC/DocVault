package com.docvault.app.di

import com.docvault.app.navigation.AppNavigator
import com.docvault.core.navigation.Navigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindNavigator(impl: AppNavigator): Navigator
}
