package com.tglee.tgaccount.data.transferfeed.vo


enum class RecentRecipientType(val value: String) {
    ACCOUNT("account"),
    PHONE("phone"),
    NONE("")
    ;

    companion object {
        fun find(type: String): RecentRecipientType =
            RecentRecipientType.entries.find {
                it.value == type
            } ?: NONE
    }
}

sealed interface RecentRecipientVO {
    val id: String
    val name: String
    val iconUrl: String

    data class Account(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val accountNumber: String,
        val bankName: String,
    ) : RecentRecipientVO

    data class Phone(
        override val id: String,
        override val name: String,
        override val iconUrl: String,
        val phoneNumber: String,
    ) : RecentRecipientVO

    data object None: RecentRecipientVO {
        override val id: String = ""
        override val name: String = ""
        override val iconUrl: String = ""
    }
}
