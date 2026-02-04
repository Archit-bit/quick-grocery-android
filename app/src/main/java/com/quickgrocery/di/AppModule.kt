package com.quickgrocery.di

import com.quickgrocery.data.GroceryRepository
import com.quickgrocery.data.InMemoryGroceryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGroceryRepository(): GroceryRepository = InMemoryGroceryRepository()
}
