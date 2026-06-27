package com.tglee.tgaccount.data.transferfeed.vo

import com.tglee.tgaccount.core.feed.FeedVO


data class FeedMyAccountVOList(
    val list: List<FeedMyAccountVO>
): FeedVO

/** 내 계좌. */
data class FeedMyAccountVO(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
    val showInCollapsed: Boolean,
): FeedVO

