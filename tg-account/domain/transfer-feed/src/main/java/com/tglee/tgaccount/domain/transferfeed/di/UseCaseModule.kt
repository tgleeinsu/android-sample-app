package com.tglee.tgaccount.domain.transferfeed.di

import com.tglee.tgaccount.domain.transferfeed.usecase.GetMyAccountsUseCase
import com.tglee.tgaccount.domain.transferfeed.usecase.GetMyAccountsUseCaseImpl
import com.tglee.tgaccount.domain.transferfeed.usecase.GetRecentRecipientsUseCase
import com.tglee.tgaccount.domain.transferfeed.usecase.GetRecentRecipientsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindGetMyAccountsUseCase(impl: GetMyAccountsUseCaseImpl): GetMyAccountsUseCase

    @Binds
    fun bindGetRecentRecipientsUseCase(impl: GetRecentRecipientsUseCaseImpl): GetRecentRecipientsUseCase
}
