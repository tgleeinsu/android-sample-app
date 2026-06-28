package com.tglee.tgaccount.domain.transferfeed.screenuistate

import com.tglee.tgaccount.core.feed.marker.FeedUiState


sealed interface TransferScreenUiState {
    val isLoading: Boolean

    data class Loading(
        override val isLoading: Boolean = true
    ) : TransferScreenUiState


    data class Loaded(
        override val isLoading: Boolean = false,
        val items: List<FeedUiState>,
    ) : TransferScreenUiState
}
