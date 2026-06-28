package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedRecentRecipientUiState
import com.tglee.tgaccount.core.feed.marker.FeedItemStateParam

/**
 * 피드 아이템들의 콜백/검색어를 화면에서 주입하기 위한 파라미터.
 * [searchKeyword] 는 검색 입력값이자 각 행의 하이라이트 기준으로 함께 쓰인다.
 */
data class TransferFeedStateParam(
    val searchKeyword: String,
    val onChangeSearchKeyword: (String) -> Unit,
    val onClearSearchKeyword: () -> Unit,
    val onToggleMyAccountMore: () -> Unit,
    val onSelectMyAccount: (FeedMyAccountUiState) -> Unit,
    val onSelectRecentAccount: (FeedRecentRecipientUiState.Account) -> Unit,
    val onSelectRecentPhone: (FeedRecentRecipientUiState.Phone) -> Unit,
) : FeedItemStateParam
