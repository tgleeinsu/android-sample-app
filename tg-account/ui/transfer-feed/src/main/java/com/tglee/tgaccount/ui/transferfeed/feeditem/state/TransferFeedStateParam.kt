package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.marker.FeedItemStateParam

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
