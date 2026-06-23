package com.tglee.tgaccount.ui.transferfeed.state

import com.tglee.tgaccount.core.feed.FeedItemState
import com.tglee.tgaccount.core.feed.FeedItemStateParam
import com.tglee.tgaccount.domain.transferfeed.uistate.MyAccountItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.RecentAccountItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.RecentPhoneItemUiState

/**
 * 피드 아이템들의 콜백을 화면에서 주입하기 위한 파라미터.
 * android_v2 의 FeedItemStateParam(mainState/navController 전달) 대응.
 */
data class TransferFeedStateParam(
    val onClickSearch: () -> Unit,
    val onToggleMyAccountMore: () -> Unit,
    val onSelectMyAccount: (MyAccountItemUiState) -> Unit,
    val onSelectRecentAccount: (RecentAccountItemUiState) -> Unit,
    val onSelectRecentPhone: (RecentPhoneItemUiState) -> Unit,
) : FeedItemStateParam

data class SearchBarState(
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = "search_bar"
}

data class MyAccountItemState(
    val uiState: MyAccountItemUiState,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}

data class MyAccountMoreButtonState(
    val expanded: Boolean,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = "my_account_more"
}

data class RecentAccountItemState(
    val uiState: RecentAccountItemUiState,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}

data class RecentPhoneItemState(
    val uiState: RecentPhoneItemUiState,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}

data class SectionHeaderState(
    val title: String,
    val headerKey: String,
) : FeedItemState {
    override fun getKey(): String = headerKey
}
