package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

data class FeedMyAccountListUiState(
    override val id: String,
    val list: List<FeedMyAccountUiState>,
): FeedUiState
