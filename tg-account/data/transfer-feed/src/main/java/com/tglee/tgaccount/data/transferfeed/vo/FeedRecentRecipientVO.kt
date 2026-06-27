package com.tglee.tgaccount.data.transferfeed.vo

import com.tglee.tgaccount.core.feed.FeedVO


enum class FeedRecentRecipientType(val value: String) {
    ACCOUNT("account"),
    PHONE("phone"),
    NONE("")
    ;

    companion object {
        fun find(type: String): FeedRecentRecipientType =
            FeedRecentRecipientType.entries.find {
                it.value == type
            } ?: NONE
    }
}

data class FeedRecentRecipientVOList(
    val list: List<FeedRecentRecipientVO>
): FeedVO

sealed interface FeedRecentRecipientVO: FeedVO {
    val id: String
    val name: String
    val iconUrl: String

    data class Account(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val accountNumber: String,
        val bankName: String,
    ) : FeedRecentRecipientVO

    data class Phone(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val phoneNumber: String,
    ) : FeedRecentRecipientVO

    data object None: FeedRecentRecipientVO {
        override val id: String = ""
        override val name: String = ""
        override val iconUrl: String = ""
    }
}
