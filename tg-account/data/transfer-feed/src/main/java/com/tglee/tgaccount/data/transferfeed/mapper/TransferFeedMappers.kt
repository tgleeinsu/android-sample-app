package com.tglee.tgaccount.data.transferfeed.mapper

import com.tglee.tgaccount.data.transferfeed.entity.MyAccountEntity
import com.tglee.tgaccount.data.transferfeed.entity.RecentRecipientEntity
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVOList
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientType
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVOList

internal fun MyAccountEntity.toVO() = FeedMyAccountVO(
    id = id,
    accountName = accountName,
    accountNumber = accountNumber,
    bankName = bankName,
    iconUrl = iconUrl,
)

internal fun List<MyAccountEntity>.toVO() =
    FeedMyAccountVOList(
        list = this.map {
            it.toVO()
        }
    )

internal fun RecentRecipientEntity.toVO(): FeedRecentRecipientVO {
    return when (FeedRecentRecipientType.find(type)) {
        FeedRecentRecipientType.ACCOUNT -> FeedRecentRecipientVO.Account(
            id = id,
            name = name,
            iconUrl = iconUrl,
            accountNumber = accountNumber,
            bankName = bankName,
        )

        FeedRecentRecipientType.PHONE -> FeedRecentRecipientVO.Phone(
            id = id,
            name = name,
            iconUrl = iconUrl,
            phoneNumber = phoneNumber,
        )

        FeedRecentRecipientType.NONE -> FeedRecentRecipientVO.None
    }
}

internal fun List<RecentRecipientEntity>.toVO() =
    FeedRecentRecipientVOList(
        list = this.map {
            it.toVO()
        }
    )
