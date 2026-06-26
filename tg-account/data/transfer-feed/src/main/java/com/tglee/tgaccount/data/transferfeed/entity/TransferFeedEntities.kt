package com.tglee.tgaccount.data.transferfeed.entity

import kotlinx.serialization.Serializable

/** 내 계좌 목록 API 응답(mock json) 엔티티. */
@Serializable
data class MyAccountEntity(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String? = null,
    val showInCollapsed: Boolean = false,
)

/** 최근 보낸 계좌 목록 API 응답(mock json) 엔티티. type 에 따라 account/phone 으로 해석. */
@Serializable
data class RecentRecipientEntity(
    val type: String,
    val id: String,
    val name: String,
    val phoneNumber: String? = null,
    val accountNumber: String? = null,
    val bankName: String? = null,
    val iconUrl: String? = null,
)
