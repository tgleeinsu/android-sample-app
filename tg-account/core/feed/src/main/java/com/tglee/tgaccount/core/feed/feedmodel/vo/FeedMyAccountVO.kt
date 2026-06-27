package com.tglee.tgaccount.core.feed.feedmodel.vo

import com.tglee.tgaccount.core.feed.marker.FeedVO



/** 내 계좌. */
data class FeedMyAccountVO(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
): FeedVO

