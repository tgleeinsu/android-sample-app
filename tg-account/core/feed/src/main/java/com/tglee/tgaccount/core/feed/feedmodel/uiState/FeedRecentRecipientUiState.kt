package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

sealed interface FeedRecentRecipientUiState: FeedUiState {
    override val id: String
    val name: String
    val iconUrl: String

    /** 방금 송금한 대상 여부. "방금 송금" 뱃지 표기에 쓰인다(런타임 조립 단계에서 채워짐). */
    val justSent: Boolean

    data class Account(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val accountNumber: String,
        val bankName: String,
        override val justSent: Boolean = false,
    ) : FeedRecentRecipientUiState

    data class Phone(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val phoneNumber: String,
        override val justSent: Boolean = false,
    ) : FeedRecentRecipientUiState

    data object None: FeedRecentRecipientUiState {
        override val id: String = ""
        override val name: String = ""
        override val iconUrl: String = ""
        override val justSent: Boolean = false
    }
}
