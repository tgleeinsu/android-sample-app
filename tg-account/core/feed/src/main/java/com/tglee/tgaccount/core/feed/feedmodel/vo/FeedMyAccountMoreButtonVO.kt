package com.tglee.tgaccount.core.feed.feedmodel.vo

import com.tglee.tgaccount.core.feed.marker.FeedVO

data class FeedMyAccountMoreButtonVO(
    val expanded: Boolean,
    val hiddenCount: Int,
) : FeedVO
