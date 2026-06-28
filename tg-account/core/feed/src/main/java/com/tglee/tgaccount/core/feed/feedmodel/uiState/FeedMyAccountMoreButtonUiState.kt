package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

data class FeedMyAccountMoreButtonUiState(
    override val id: String,
    val expanded: Boolean,
    val hiddenCount: Int,
) : FeedUiState
