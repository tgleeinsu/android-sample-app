package com.tglee.tgaccount.data.transferfeed.mapper

import com.tglee.tgaccount.data.transferfeed.entity.MyAccountEntity
import com.tglee.tgaccount.data.transferfeed.entity.RecentRecipientEntity
import com.tglee.tgaccount.data.transferfeed.model.MyAccountVO
import com.tglee.tgaccount.data.transferfeed.model.RecentRecipientVO

internal fun MyAccountEntity.toVO(): MyAccountVO = MyAccountVO(
    id = id,
    accountName = accountName,
    accountNumber = accountNumber,
    bankName = bankName,
    iconUrl = iconUrl,
    showInCollapsed = showInCollapsed,
)

/** type 필드로 account/phone 을 구분해 sealed VO 로 변환. 알 수 없는 type 은 제외. */
internal fun RecentRecipientEntity.toVO(): RecentRecipientVO? = when (type) {
    "account" -> RecentRecipientVO.Account(
        id = id,
        name = name,
        iconUrl = iconUrl,
        accountNumber = accountNumber.orEmpty(),
        bankName = bankName.orEmpty(),
    )

    "phone" -> RecentRecipientVO.Phone(
        id = id,
        name = name,
        iconUrl = iconUrl,
        phoneNumber = phoneNumber.orEmpty(),
    )

    else -> null
}
