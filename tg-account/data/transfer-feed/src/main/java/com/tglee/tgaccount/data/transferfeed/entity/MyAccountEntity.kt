@file:OptIn(InternalSerializationApi::class)
package com.tglee.tgaccount.data.transferfeed.entity

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class MyAccountEntity(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String = "",
    val showInCollapsed: Boolean,
)
