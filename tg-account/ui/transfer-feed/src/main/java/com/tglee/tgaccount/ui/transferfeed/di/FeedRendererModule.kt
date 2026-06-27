package com.tglee.tgaccount.ui.transferfeed.di

import androidx.compose.runtime.Composable
import com.tglee.tgaccount.core.feed.marker.FeedItemRenderer
import com.tglee.tgaccount.core.feed.marker.FeedItemState
import com.tglee.tgaccount.ui.transferfeed.item.MyAccountItem
import com.tglee.tgaccount.ui.transferfeed.item.MyAccountMoreButton
import com.tglee.tgaccount.ui.transferfeed.item.RecentAccountItem
import com.tglee.tgaccount.ui.transferfeed.item.RecentPhoneItem
import com.tglee.tgaccount.ui.transferfeed.item.SearchBarItem
import com.tglee.tgaccount.ui.transferfeed.item.SectionHeader
import com.tglee.tgaccount.ui.transferfeed.state.MyAccountItemState
import com.tglee.tgaccount.ui.transferfeed.state.MyAccountMoreButtonState
import com.tglee.tgaccount.ui.transferfeed.state.RecentAccountItemState
import com.tglee.tgaccount.ui.transferfeed.state.RecentPhoneItemState
import com.tglee.tgaccount.ui.transferfeed.state.SearchBarState
import com.tglee.tgaccount.ui.transferfeed.state.SectionHeaderState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

/**
 * 계층5: FeedItemState -> Composable 렌더러 등록 (@IntoMap @ClassKey(State)).
 * 운영 프로젝트에선 @UniversalItem + KSP 가 이 등록을 자동 생성한다.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object FeedRendererModule {

    @Provides
    @IntoMap
    @ClassKey(SearchBarState::class)
    fun searchBar(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = SearchBarItem(state as SearchBarState)
    }

    @Provides
    @IntoMap
    @ClassKey(MyAccountItemState::class)
    fun myAccount(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = MyAccountItem(state as MyAccountItemState)
    }

    @Provides
    @IntoMap
    @ClassKey(MyAccountMoreButtonState::class)
    fun myAccountMore(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = MyAccountMoreButton(state as MyAccountMoreButtonState)
    }

    @Provides
    @IntoMap
    @ClassKey(RecentAccountItemState::class)
    fun recentAccount(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = RecentAccountItem(state as RecentAccountItemState)
    }

    @Provides
    @IntoMap
    @ClassKey(RecentPhoneItemState::class)
    fun recentPhone(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = RecentPhoneItem(state as RecentPhoneItemState)
    }

    @Provides
    @IntoMap
    @ClassKey(SectionHeaderState::class)
    fun sectionHeader(): FeedItemRenderer = object : FeedItemRenderer {
        @Composable
        override fun Render(state: FeedItemState) = SectionHeader(state as SectionHeaderState)
    }
}
