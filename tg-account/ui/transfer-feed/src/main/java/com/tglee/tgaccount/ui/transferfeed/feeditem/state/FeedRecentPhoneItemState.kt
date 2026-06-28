package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedRecentRecipientUiState
import com.tglee.tgaccount.core.feed.marker.FeedItemState

data class FeedRecentPhoneItemState(
    val uiState: FeedRecentRecipientUiState.Phone,
    val query: String,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}
