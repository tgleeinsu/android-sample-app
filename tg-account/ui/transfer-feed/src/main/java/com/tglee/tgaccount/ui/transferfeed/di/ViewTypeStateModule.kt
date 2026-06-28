package com.tglee.tgaccount.ui.transferfeed.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountMoreButtonUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedRecentRecipientUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedSectionHeaderUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedTransferSearchBarUiState
import com.tglee.tgaccount.core.feed.marker.FeedItemState
import com.tglee.tgaccount.core.feed.marker.FeedItemStateParam
import com.tglee.tgaccount.core.feed.marker.ViewTypeStateProvider
import com.tglee.tgaccount.ui.transferfeed.feeditem.event.TransferFeedEvent
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
internal object ViewTypeStateModule {

    @Provides
    @IntoMap
    @ClassKey(FeedTransferSearchBarUiState::class)
    fun searchBar(): ViewTypeStateProvider<*> =
        object : ViewTypeStateProvider<FeedTransferSearchBarUiState> {
            @Composable
            override fun rememberState(
                uiState: FeedTransferSearchBarUiState,
                param: FeedItemStateParam
            ): FeedItemState =
                remember(uiState, param) {
                    FeedSearchBarState(
                        uiState = uiState,
                        value = uiState.searchKeyword,
                        onValueChange = { param.onEvent(TransferFeedEvent.ChangeSearchKeyword(it)) },
                        onClear = { param.onEvent(TransferFeedEvent.ClearSearchKeyword) },
                    )
                }
        }

    @Provides
    @IntoMap
    @ClassKey(FeedMyAccountUiState::class)
    fun myAccount(): ViewTypeStateProvider<*> =
        object : ViewTypeStateProvider<FeedMyAccountUiState> {
            @Composable
            override fun rememberState(
                uiState: FeedMyAccountUiState,
                param: FeedItemStateParam
            ): FeedItemState =
                remember(uiState, param) {
                    FeedMyAccountItemState(
                        uiState = uiState,
                        onClick = { param.onEvent(TransferFeedEvent.SelectMyAccount(uiState)) },
                    )
                }
        }

    @Provides
    @IntoMap
    @ClassKey(FeedMyAccountMoreButtonUiState::class)
    fun myAccountMore(): ViewTypeStateProvider<*> =
        object : ViewTypeStateProvider<FeedMyAccountMoreButtonUiState> {
            @Composable
            override fun rememberState(
                uiState: FeedMyAccountMoreButtonUiState,
                param: FeedItemStateParam
            ): FeedItemState =
                remember(uiState, param) {
                    FeedMyAccountMoreButtonState(
                        uiState.expanded,
                        uiState.hiddenCount,
                        onClick = { param.onEvent(TransferFeedEvent.ToggleMyAccountMore) },
                    )
                }
        }

    @Provides
    @IntoMap
    @ClassKey(FeedRecentRecipientUiState.Account::class)
    fun recentAccount(): ViewTypeStateProvider<*> =
        object : ViewTypeStateProvider<FeedRecentRecipientUiState.Account> {
            @Composable
            override fun rememberState(
                uiState: FeedRecentRecipientUiState.Account,
                param: FeedItemStateParam
            ): FeedItemState =
                remember(uiState, param) {
                    FeedRecentAccountItemState(
                        uiState = uiState,
                        query = "", // TODO 검색 하이라이트: 조립 UseCase 가 UiState 에 검색어를 stamp 할 예정
                        onClick = { param.onEvent(TransferFeedEvent.SelectRecentAccount(uiState)) },
                    )
                }
        }

    @Provides
    @IntoMap
    @ClassKey(FeedRecentRecipientUiState.Phone::class)
    fun recentPhone(): ViewTypeStateProvider<*> =
        object : ViewTypeStateProvider<FeedRecentRecipientUiState.Phone> {
            @Composable
            override fun rememberState(
                uiState: FeedRecentRecipientUiState.Phone,
                param: FeedItemStateParam
            ): FeedItemState =
                remember(uiState, param) {
                    FeedRecentPhoneItemState(
                        uiState = uiState,
                        query = "", // TODO 검색 하이라이트: 조립 UseCase 가 UiState 에 검색어를 stamp 할 예정
                        onClick = { param.onEvent(TransferFeedEvent.SelectRecentPhone(uiState)) },
                    )
                }
        }

    @Provides
    @IntoMap
    @ClassKey(FeedSectionHeaderUiState::class)
    fun sectionHeader(): ViewTypeStateProvider<*> =
        object : ViewTypeStateProvider<FeedSectionHeaderUiState> {
            @Composable
            override fun rememberState(
                uiState: FeedSectionHeaderUiState,
                param: FeedItemStateParam
            ): FeedItemState =
                remember(uiState) {
                    FeedSectionHeaderState(
                        title = uiState.title,
                        headerKey = uiState.id
                    )
                }
        }
}
