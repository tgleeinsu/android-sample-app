package com.tglee.tgaccount.domain.transferfeed.vo

/** 내 계좌. (도메인 모델) */
data class MyAccountVO(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
    /** 내 계좌 목록이 축소 상태일 때 표시할지 여부. */
    val visibleWhenCollapsed: Boolean,
)

/** 최근 보낸 상대방. account/phone 두 타입을 sealed 로 구분(멀티바인딩 2뷰타입). */
sealed interface RecentRecipientVO {
    val id: String
    val name: String
    val iconUrl: String?

    data class Account(
        override val id: String,
        override val name: String,
        override val iconUrl: String?,
        val accountNumber: String,
        val bankName: String,
    ) : RecentRecipientVO

    data class Phone(
        override val id: String,
        override val name: String,
        override val iconUrl: String?,
        val phoneNumber: String,
    ) : RecentRecipientVO
}
