package com.tglee.tgaccount.data.transfersend.di

import com.tglee.tgaccount.data.transfersend.datasource.FakeTransferSendDataSource
import com.tglee.tgaccount.data.transfersend.datasource.TransferSendDataSource
import com.tglee.tgaccount.data.transfersend.repository.TransferSendRepository
import com.tglee.tgaccount.data.transfersend.repository.TransferSendRepositoryImpl
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
    fun bindTransferSendDataSource(impl: FakeTransferSendDataSource): TransferSendDataSource

    @Binds
    @Singleton
    fun bindTransferSendRepository(impl: TransferSendRepositoryImpl): TransferSendRepository
}
