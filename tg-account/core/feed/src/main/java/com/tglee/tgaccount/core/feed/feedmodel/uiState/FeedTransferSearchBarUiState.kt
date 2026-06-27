package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

data class FeedTransferSearchBarUiState(
    override val id: String,
    val searchKeyword: String,
): FeedUiState
