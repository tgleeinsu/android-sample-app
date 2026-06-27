package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

sealed interface FeedRecentRecipientUiState: FeedUiState {
    override val id: String
    val name: String
    val iconUrl: String

    data class Account(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val accountNumber: String,
        val bankName: String,
    ) : FeedRecentRecipientUiState

    data class Phone(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val phoneNumber: String,
    ) : FeedRecentRecipientUiState

    data object None: FeedRecentRecipientUiState {
        override val id: String = ""
        override val name: String = ""
        override val iconUrl: String = ""
    }
}
