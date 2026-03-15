package com.docvault.app.di

import com.docvault.core.navigation.Navigator
import com.docvault.test.utils.fake.FakeNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NavigationModule::class]
)
object TestNavigationModule {

    @Provides
    @Singleton
    fun provideFakeNavigator(): Navigator = FakeNavigator()
}