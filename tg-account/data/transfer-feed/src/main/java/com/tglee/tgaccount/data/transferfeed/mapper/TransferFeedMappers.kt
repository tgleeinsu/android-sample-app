package com.tglee.tgaccount.data.transferfeed.mapper

import com.tglee.tgaccount.data.transferfeed.entity.MyAccountEntity
import com.tglee.tgaccount.data.transferfeed.entity.RecentRecipientEntity
import com.tglee.tgaccount.data.transferfeed.vo.MyAccountVO
import com.tglee.tgaccount.data.transferfeed.vo.RecentRecipientType
import com.tglee.tgaccount.data.transferfeed.vo.RecentRecipientVO

internal fun MyAccountEntity.toVO() = MyAccountVO(
    id = id,
    accountName = accountName,
    accountNumber = accountNumber,
    bankName = bankName,
    iconUrl = iconUrl,
    showInCollapsed = showInCollapsed,
)

internal fun RecentRecipientEntity.toVO(): RecentRecipientVO {
    return when (RecentRecipientType.find(type)) {
        RecentRecipientType.ACCOUNT -> RecentRecipientVO.Account(
            id = id,
            name = name,
            iconUrl = iconUrl,
            accountNumber = accountNumber,
            bankName = bankName,
        )

        RecentRecipientType.PHONE -> RecentRecipientVO.Phone(
            id = id,
            name = name,
            iconUrl = iconUrl,
            phoneNumber = phoneNumber,
        )

        RecentRecipientType.NONE -> RecentRecipientVO.None
    }
}