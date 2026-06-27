package com.tglee.tgaccount.core.feed.feedmodel.uiState

import com.tglee.tgaccount.core.feed.marker.FeedUiState

/** 내 계좌. */
data class FeedMyAccountUiState(
    override val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
): FeedUiState