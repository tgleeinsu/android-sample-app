package com.tglee.tgaccount.core.feed.mapper

import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedMyAccountEntity
import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedMyAccountEntityList
import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedRecentRecipientEntity
import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedRecentRecipientEntityList
import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedTransferSearchBarEntity
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedMyAccountVO
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedMyAccountVOList
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedRecentRecipientType
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedRecentRecipientVO
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedRecentRecipientVOList
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedTransferSearchBarVO
import com.tglee.tgaccount.core.feed.feedmodel.vo.NotSupportedViewTypeVO
import com.tglee.tgaccount.core.feed.marker.FeedEntity
import com.tglee.tgaccount.core.feed.marker.FeedVO
import javax.inject.Inject


interface FeedEntityToVoMapper {
    fun entityToVO(entity: FeedEntity): FeedVO
}


class FeedEntityToVOMapperImpl @Inject constructor() : FeedEntityToVoMapper {
    override fun entityToVO(entity: FeedEntity): FeedVO {
        return with(entity) {
            when (this) {
                is FeedTransferSearchBarEntity -> {
                    FeedTransferSearchBarVO(
                        searchKeyword = searchKeyword
                    )
                }

                is FeedMyAccountEntity -> {
                    myAccountToVO(this)
                }

                is FeedMyAccountEntityList -> {
                    FeedMyAccountVOList(
                        list = list.map {
                            myAccountToVO(it)
                        }
                    )
                }


                is FeedRecentRecipientEntityList -> {
                    FeedRecentRecipientVOList(
                        list = list.map {
                            recentRecipientToVO(it)
                        }
                    )
                }

                is FeedRecentRecipientEntity -> recentRecipientToVO(this)

                else -> NotSupportedViewTypeVO
            }
        }
    }
}

private fun recentRecipientToVO(
    recent: FeedRecentRecipientEntity
): FeedRecentRecipientVO {
    return with(recent) {
        when(FeedRecentRecipientType.find(recent.type)) {
            FeedRecentRecipientType.ACCOUNT -> {
                FeedRecentRecipientVO.Account(
                    id = id,
                    name = name,
                    iconUrl = iconUrl,
                    accountNumber = accountNumber,
                    bankName = bankName
                )
            }

            FeedRecentRecipientType.PHONE -> {
                FeedRecentRecipientVO.Phone(
                    id = id,
                    name = name,
                    iconUrl = iconUrl,
                    phoneNumber = phoneNumber
                )
            }

            FeedRecentRecipientType.NONE -> FeedRecentRecipientVO.None
        }
    }
}

private fun myAccountToVO(
    my: FeedMyAccountEntity
): FeedMyAccountVO {
    return with(my) {
        FeedMyAccountVO(
            id = id,
            accountName = accountName,
            accountNumber = accountNumber,
            bankName = bankName,
            iconUrl = iconUrl
        )
    }
}