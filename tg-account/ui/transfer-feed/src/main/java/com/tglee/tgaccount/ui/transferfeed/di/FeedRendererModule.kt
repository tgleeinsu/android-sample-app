package com.tglee.tgaccount.ui.transferfeed.di

import androidx.compose.runtime.Composable
import com.tglee.tgaccount.core.feed.marker.FeedItemRenderer
import com.tglee.tgaccount.core.feed.marker.FeedItemState
import com.tglee.tgaccount.ui.transferfeed.feeditem.MyAccountItem
import com.tglee.tgaccount.ui.transferfeed.feeditem.MyAccountMoreButton
import com.tglee.tgaccount.ui.transferfeed.feeditem.RecentAccountItem
import com.tglee.tgaccount.ui.transferfeed.feeditem.RecentPhoneItem
import com.tglee.tgaccount.ui.transferfeed.feeditem.SearchBarItem
import com.tglee.tgaccount.ui.transferfeed.feeditem.SectionHeader
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedMyAccountItemState
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedMyAccountMoreButtonState
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedRecentAccountItemState
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedRecentPhoneItemState
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedSearchBarState
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedSectionHeaderState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
internal object FeedRendererModule {

    @Provides
    @IntoMap
    @ClassKey(FeedSearchBarState::class)
    fun searchBar(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = SearchBarItem(state as FeedSearchBarState)
    }

    @Provides
    @IntoMap
    @ClassKey(FeedMyAccountItemState::class)
    fun myAccount(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = MyAccountItem(state as FeedMyAccountItemState)
    }

    @Provides
    @IntoMap
    @ClassKey(FeedMyAccountMoreButtonState::class)
    fun myAccountMore(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = MyAccountMoreButton(state as FeedMyAccountMoreButtonState)
    }

    @Provides
    @IntoMap
    @ClassKey(FeedRecentAccountItemState::class)
    fun recentAccount(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = RecentAccountItem(state as FeedRecentAccountItemState)
    }

    @Provides
    @IntoMap
    @ClassKey(FeedRecentPhoneItemState::class)
    fun recentPhone(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = RecentPhoneItem(state as FeedRecentPhoneItemState)
    }

    @Provides
    @IntoMap
    @ClassKey(FeedSectionHeaderState::class)
    fun sectionHeader(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = SectionHeader(state as FeedSectionHeaderState)
    }
}
