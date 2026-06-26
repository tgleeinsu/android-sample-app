package com.tglee.tgaccount.data.transferfeed.vo

import com.tglee.tgaccount.data.transferfeed.entity.MyAccountEntity

/** 내 계좌. */
data class MyAccountVO(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
    val showInCollapsed: Boolean,
)

