package com.tglee.tgaccount.data.transferfeed.di

import com.tglee.tgaccount.data.transferfeed.repository.TransferFeedViewTypeRepository
import com.tglee.tgaccount.data.transferfeed.repository.TransferFeedViewTypeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindTransferFeedViewTypeRepository(impl: TransferFeedViewTypeRepositoryImpl): TransferFeedViewTypeRepository
}