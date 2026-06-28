package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.marker.FeedItemState

data class FeedMyAccountMoreButtonState(
    // val uiState // TODO
    val expanded: Boolean,
    val hiddenCount: Int,
    val onClick: () -> Unit,
) : FeedItemState {
    override fun getKey(): String = "my_account_more"
}
