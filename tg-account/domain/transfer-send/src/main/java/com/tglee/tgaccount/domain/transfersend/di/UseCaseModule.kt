package com.tglee.tgaccount.domain.transfersend.di

import com.tglee.tgaccount.domain.transfersend.usecase.SendMoneyUseCase
import com.tglee.tgaccount.domain.transfersend.usecase.SendMoneyUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindSendMoneyUseCase(impl: SendMoneyUseCaseImpl): SendMoneyUseCase
}
