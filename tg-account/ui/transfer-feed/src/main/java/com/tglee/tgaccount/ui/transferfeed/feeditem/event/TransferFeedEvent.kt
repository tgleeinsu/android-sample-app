package com.tglee.tgaccount.ui.transferfeed.feeditem.event

import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedRecentRecipientUiState
import com.tglee.tgaccount.core.feed.marker.FeedEvent

/**
 * transfer 피드 아이템의 상호작용 이벤트.
 * provider(계층4)가 발행하고 화면([rememberTransferScreenState])이 처리한다.
 * 공용 [FeedEvent] 를 구현하므로 provider 는 화면 전용 param 에 의존하지 않는다.
 */
sealed interface TransferFeedEvent : FeedEvent {
    data class SelectMyAccount(val uiState: FeedMyAccountUiState) : TransferFeedEvent
    data class SelectRecentAccount(val uiState: FeedRecentRecipientUiState.Account) : TransferFeedEvent
    data class SelectRecentPhone(val uiState: FeedRecentRecipientUiState.Phone) : TransferFeedEvent
    data object ToggleMyAccountMore : TransferFeedEvent
    data class ChangeSearchKeyword(val keyword: String) : TransferFeedEvent
    data object ClearSearchKeyword : TransferFeedEvent
}
