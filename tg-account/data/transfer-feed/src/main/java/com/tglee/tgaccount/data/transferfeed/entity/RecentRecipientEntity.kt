@file:OptIn(InternalSerializationApi::class)
package com.tglee.tgaccount.data.transferfeed.entity

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** 최근 보낸 계좌 목록 API 응답(mock json) 엔티티. type 에 따라 account/phone 으로 해석. */
@Serializable
data class RecentRecipientEntity(
    val type: String,
    val id: String,
    val name: String,
    val phoneNumber: String = "",
    val accountNumber: String = "",
    val bankName: String = "",
    val iconUrl: String,
)
