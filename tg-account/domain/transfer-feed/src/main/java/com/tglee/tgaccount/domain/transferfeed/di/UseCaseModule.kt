package com.tglee.tgaccount.domain.transferfeed.di

import com.tglee.tgaccount.domain.transferfeed.usecase.GetTransferScreenUiStateUseCase
import com.tglee.tgaccount.domain.transferfeed.usecase.GetTransferScreenUiStateUseCaseImpl
import com.tglee.tgaccount.domain.transferfeed.usecase.LoadTransferFeedUseCase
import com.tglee.tgaccount.domain.transferfeed.usecase.LoadTransferFeedUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindLoadTransferFeedUseCase(impl: LoadTransferFeedUseCaseImpl): LoadTransferFeedUseCase

    @Binds
    fun bindGetTransferScreenUiStateUseCase(impl: GetTransferScreenUiStateUseCaseImpl): GetTransferScreenUiStateUseCase
}
