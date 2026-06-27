@file:OptIn(InternalSerializationApi::class)

package com.tglee.tgaccount.core.feed.feedmodel.entity

import com.tglee.tgaccount.core.feed.marker.FeedEntity
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class FeedMyAccountEntity(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String = "",
): FeedEntity

data class FeedMyAccountEntityList(
    val list: List<FeedMyAccountEntity>
): FeedEntity
