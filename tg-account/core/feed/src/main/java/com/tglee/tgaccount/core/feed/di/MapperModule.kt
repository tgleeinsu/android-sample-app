package com.tglee.tgaccount.core.feed.di

import com.tglee.tgaccount.core.feed.mapper.FeedEntityToVOMapper
import com.tglee.tgaccount.core.feed.mapper.FeedEntityToVOMapperImpl
import com.tglee.tgaccount.core.feed.mapper.FeedVOToUiStateMapper
import com.tglee.tgaccount.core.feed.mapper.FeedVOToUiStateMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface MapperModule {

    @Binds
    fun bindFeedEntityToVOMapper(impl: FeedEntityToVOMapperImpl): FeedEntityToVOMapper

    @Binds
    fun bindFeedVOToUiStateMapper(impl: FeedVOToUiStateMapperImpl): FeedVOToUiStateMapper
}
