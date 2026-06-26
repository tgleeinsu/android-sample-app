package com.tglee.tgaccount.ui.transferfeed.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tglee.tgaccount.core.feed.FeedItemState
import com.tglee.tgaccount.core.feed.FeedItemStateParam
import com.tglee.tgaccount.core.feed.ViewTypeStateProvider
import com.tglee.tgaccount.domain.transferfeed.uistate.FeedSectionHeaderUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.MyAccountItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.MyAccountMoreButtonUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.RecentAccountItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.RecentPhoneItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.SearchBarUiState
import com.tglee.tgaccount.ui.transferfeed.state.MyAccountItemState
import com.tglee.tgaccount.ui.transferfeed.state.MyAccountMoreButtonState
import com.tglee.tgaccount.ui.transferfeed.state.RecentAccountItemState
import com.tglee.tgaccount.ui.transferfeed.state.RecentPhoneItemState
import com.tglee.tgaccount.ui.transferfeed.state.SearchBarState
import com.tglee.tgaccount.ui.transferfeed.state.SectionHeaderState
import com.tglee.tgaccount.ui.transferfeed.state.TransferFeedStateParam
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

/**
 * 계층4: UiState -> FeedItemState 변환기 등록 (@IntoMap @ClassKey(UiState)).
 */
@Module
@InstallIn(SingletonComponent::class)
internal object ViewTypeStateModule {

    @Provides
    @IntoMap
    @ClassKey(SearchBarUiState::class)
    fun searchBar(): ViewTypeStateProvider<*> = object : ViewTypeStateProvider<SearchBarUiState> {
        @Composable
        override fun rememberState(uiState: SearchBarUiState, param: FeedItemStateParam): FeedItemState? {
            val p = param as? TransferFeedStateParam ?: return null
            return remember(uiState.query, p) { SearchBarState(uiState.query, p.onQueryChange, p.onClearQuery) }
        }
    }

    @Provides
    @IntoMap
    @ClassKey(MyAccountItemUiState::class)
    fun myAccount(): ViewTypeStateProvider<*> = object : ViewTypeStateProvider<MyAccountItemUiState> {
        @Composable
        override fun rememberState(uiState: MyAccountItemUiState, param: FeedItemStateParam): FeedItemState? {
            val p = param as? TransferFeedStateParam ?: return null
            return remember(uiState, p) { MyAccountItemState(uiState, p.query) { p.onSelectMyAccount(uiState) } }
        }
    }

    @Provides
    @IntoMap
    @ClassKey(MyAccountMoreButtonUiState::class)
    fun myAccountMore(): ViewTypeStateProvider<*> = object : ViewTypeStateProvider<MyAccountMoreButtonUiState> {
        @Composable
        override fun rememberState(uiState: MyAccountMoreButtonUiState, param: FeedItemStateParam): FeedItemState? {
            val p = param as? TransferFeedStateParam ?: return null
            return remember(uiState, p) {
                MyAccountMoreButtonState(uiState.expanded, uiState.hiddenCount, p.onToggleMyAccountMore)
            }
        }
    }

    @Provides
    @IntoMap
    @ClassKey(RecentAccountItemUiState::class)
    fun recentAccount(): ViewTypeStateProvider<*> = object : ViewTypeStateProvider<RecentAccountItemUiState> {
        @Composable
        override fun rememberState(uiState: RecentAccountItemUiState, param: FeedItemStateParam): FeedItemState? {
            val p = param as? TransferFeedStateParam ?: return null
            return remember(uiState, p) { RecentAccountItemState(uiState, p.query) { p.onSelectRecentAccount(uiState) } }
        }
    }

    @Provides
    @IntoMap
    @ClassKey(RecentPhoneItemUiState::class)
    fun recentPhone(): ViewTypeStateProvider<*> = object : ViewTypeStateProvider<RecentPhoneItemUiState> {
        @Composable
        override fun rememberState(uiState: RecentPhoneItemUiState, param: FeedItemStateParam): FeedItemState? {
            val p = param as? TransferFeedStateParam ?: return null
            return remember(uiState, p) { RecentPhoneItemState(uiState, p.query) { p.onSelectRecentPhone(uiState) } }
        }
    }

    @Provides
    @IntoMap
    @ClassKey(FeedSectionHeaderUiState::class)
    fun sectionHeader(): ViewTypeStateProvider<*> = object : ViewTypeStateProvider<FeedSectionHeaderUiState> {
        @Composable
        override fun rememberState(uiState: FeedSectionHeaderUiState, param: FeedItemStateParam): FeedItemState =
            remember(uiState) { SectionHeaderState(title = uiState.title, headerKey = uiState.id) }
    }
}
