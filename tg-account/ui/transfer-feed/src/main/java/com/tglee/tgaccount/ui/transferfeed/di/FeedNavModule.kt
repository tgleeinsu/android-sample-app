package com.tglee.tgaccount.ui.transferfeed.di

import com.tglee.tgaccount.core.navigation.NavEntryProvider
import com.tglee.tgaccount.ui.transferfeed.navigation.TransferFeedEntryProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal interface TransferFeedNavModule {
    @Binds
    @IntoSet
    fun bindEntryProvider(impl: TransferFeedEntryProvider): NavEntryProvider
}
