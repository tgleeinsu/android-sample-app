package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

data class FeedRecentRecipientListUiState(
    override val id: String,
    val list: List<FeedRecentRecipientUiState>,
): FeedUiState
