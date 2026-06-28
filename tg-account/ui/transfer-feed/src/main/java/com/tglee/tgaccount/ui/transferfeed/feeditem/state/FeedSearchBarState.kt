package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedTransferSearchBarUiState
import com.tglee.tgaccount.core.feed.marker.FeedItemState

data class FeedSearchBarState(
    val uiState: FeedTransferSearchBarUiState,
    val value: String,
    val onValueChange: (String) -> Unit,
    val onClear: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = uiState.id
}
