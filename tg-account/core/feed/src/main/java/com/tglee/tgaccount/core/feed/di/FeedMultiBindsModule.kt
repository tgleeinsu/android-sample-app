package com.tglee.tgaccount.core.feed.di

import com.tglee.tgaccount.core.feed.marker.FeedItemRenderer
import com.tglee.tgaccount.core.feed.marker.ViewTypeStateProvider
import dagger.Module
import dagger.multibindings.Multibinds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 중앙 집중이 아닌 feature 모듈 각각에서 FeedUiState에 대한 UI와 ScreenState를 가지고 있을 수 있도록 멀티 바인딩
 */
@Module
@InstallIn(SingletonComponent::class)
interface FeedMultiBindsModule {

    @Multibinds
    fun stateProviders(): Map<Class<*>, ViewTypeStateProvider<*>>

    @Multibinds
    fun renderers(): Map<Class<*>, FeedItemRenderer>
}
