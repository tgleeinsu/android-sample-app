package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

data class FeedSectionHeaderUiState(
    override val id: String,
    val title: String,
) : FeedUiState
