package com.tglee.tgaccount.core.feed.feedmodel.entity

import com.tglee.tgaccount.core.feed.marker.FeedEntity

data class FeedMyAccountMoreButtonEntity(
    val expanded: Boolean,
    val hiddenCount: Int,
) : FeedEntity
