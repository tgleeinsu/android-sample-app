package com.tglee.tgaccount.data.transferfeed.di

import com.tglee.tgaccount.data.transferfeed.repository.MyAccountRepository
import com.tglee.tgaccount.data.transferfeed.repository.MyAccountRepositoryImpl
import com.tglee.tgaccount.data.transferfeed.repository.RecentRecipientRepository
import com.tglee.tgaccount.data.transferfeed.repository.RecentRecipientRepositoryImpl
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
    fun bindMyAccountRepository(impl: MyAccountRepositoryImpl): MyAccountRepository

    @Binds
    @Singleton
    fun bindRecentRecipientRepository(impl: RecentRecipientRepositoryImpl): RecentRecipientRepository
}