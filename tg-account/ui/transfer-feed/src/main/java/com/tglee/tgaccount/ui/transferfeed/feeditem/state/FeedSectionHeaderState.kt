package com.tglee.tgaccount.ui.transferfeed.feeditem.state

import com.tglee.tgaccount.core.feed.marker.FeedItemState

data class FeedSectionHeaderState(
    // val uiState // TODO
    val title: String,
    val headerKey: String,
) : FeedItemState {
    override fun getKey(): String = headerKey
}
