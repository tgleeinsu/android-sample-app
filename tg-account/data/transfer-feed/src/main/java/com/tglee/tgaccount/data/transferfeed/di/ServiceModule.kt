package com.tglee.tgaccount.data.transferfeed.di

import com.tglee.tgaccount.data.transferfeed.service.FakeMyAccountService
import com.tglee.tgaccount.data.transferfeed.service.FakeRecentRecipientService
import com.tglee.tgaccount.data.transferfeed.service.MyAccountService
import com.tglee.tgaccount.data.transferfeed.service.RecentRecipientService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ServiceModule {

    @Binds
    @Singleton
    fun bindMyAccountService(impl: FakeMyAccountService): MyAccountService

    @Binds
    @Singleton
    fun bindRecentRecipientService(impl: FakeRecentRecipientService): RecentRecipientService


}
