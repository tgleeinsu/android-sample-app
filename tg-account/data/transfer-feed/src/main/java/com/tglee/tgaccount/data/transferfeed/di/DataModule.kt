package com.tglee.tgaccount.data.transferfeed.di

import com.tglee.tgaccount.data.transferfeed.repository.MyAccountRepositoryImpl
import com.tglee.tgaccount.data.transferfeed.repository.RecentRecipientRepositoryImpl
import com.tglee.tgaccount.data.transferfeed.service.FakeMyAccountService
import com.tglee.tgaccount.data.transferfeed.service.FakeRecentRecipientService
import com.tglee.tgaccount.data.transferfeed.service.MyAccountService
import com.tglee.tgaccount.data.transferfeed.service.RecentRecipientService
import com.tglee.tgaccount.domain.transferfeed.repository.MyAccountRepository
import com.tglee.tgaccount.domain.transferfeed.repository.RecentRecipientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    @Singleton
    fun bindMyAccountService(impl: FakeMyAccountService): MyAccountService

    @Binds
    @Singleton
    fun bindRecentRecipientService(impl: FakeRecentRecipientService): RecentRecipientService

    @Binds
    @Singleton
    fun bindMyAccountRepository(impl: MyAccountRepositoryImpl): MyAccountRepository

    @Binds
    @Singleton
    fun bindRecentRecipientRepository(impl: RecentRecipientRepositoryImpl): RecentRecipientRepository
}
