package com.tglee.tgaccount.core.feed.di

import com.tglee.tgaccount.core.feed.FeedItemRenderer
import com.tglee.tgaccount.core.feed.ViewTypeStateProvider
import dagger.Module
import dagger.multibindings.Multibinds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 어떤 feature 도 아이템을 등록하지 않은 상황에서도 빈 맵 주입이 가능하도록 @Multibinds 선언.
 */
@Module
@InstallIn(SingletonComponent::class)
interface FeedMultiBindsModule {

    @Multibinds
    fun stateProviders(): Map<Class<*>, ViewTypeStateProvider<*>>

    @Multibinds
    fun renderers(): Map<Class<*>, FeedItemRenderer>
}
