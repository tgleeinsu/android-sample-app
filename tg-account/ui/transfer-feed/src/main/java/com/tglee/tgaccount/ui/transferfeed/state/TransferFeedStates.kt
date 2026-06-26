package com.tglee.tgaccount.ui.transferfeed.state

import com.tglee.tgaccount.core.feed.FeedItemState
import com.tglee.tgaccount.core.feed.FeedItemStateParam
import com.tglee.tgaccount.ui.transferfeed.uistate.MyAccountItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.RecentAccountItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.RecentPhoneItemUiState

/**
 * 피드 아이템들의 콜백/검색어를 화면에서 주입하기 위한 파라미터.
 * [query] 는 검색 입력값이자 각 행의 하이라이트 기준으로 함께 쓰인다.
 */
data class TransferFeedStateParam(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onClearQuery: () -> Unit,
    val onToggleMyAccountMore: () -> Unit,
    val onSelectMyAccount: (MyAccountItemUiState) -> Unit,
    val onSelectRecentAccount: (RecentAccountItemUiState) -> Unit,
    val onSelectRecentPhone: (RecentPhoneItemUiState) -> Unit,
) : FeedItemStateParam

data class SearchBarState(
    val value: String,
    val onValueChange: (String) -> Unit,
    val onClear: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = "search_bar"
}

data class MyAccountItemState(
    val uiState: MyAccountItemUiState,
    val query: String,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}

data class MyAccountMoreButtonState(
    val expanded: Boolean,
    val hiddenCount: Int,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = "my_account_more"
}

data class RecentAccountItemState(
    val uiState: RecentAccountItemUiState,
    val query: String,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}

data class RecentPhoneItemState(
    val uiState: RecentPhoneItemUiState,
    val query: String,
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
