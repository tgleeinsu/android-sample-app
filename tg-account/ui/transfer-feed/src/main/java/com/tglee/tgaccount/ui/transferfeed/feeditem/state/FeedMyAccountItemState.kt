package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountUiState
import com.tglee.tgaccount.core.feed.marker.FeedItemState

data class FeedMyAccountItemState(
    val uiState: FeedMyAccountUiState,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}
