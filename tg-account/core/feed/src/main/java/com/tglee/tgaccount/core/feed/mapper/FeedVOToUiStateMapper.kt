package com.tglee.tgaccount.core.feed.mapper

import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountListUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedMyAccountUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedRecentRecipientListUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedRecentRecipientUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.FeedTransferSearchBarUiState
import com.tglee.tgaccount.core.feed.feedmodel.uiState.NotSupportedUiState
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedMyAccountVO
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedMyAccountVOList
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedRecentRecipientVO
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedRecentRecipientVOList
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedTransferSearchBarVO
import com.tglee.tgaccount.core.feed.marker.FeedUiState
import com.tglee.tgaccount.core.feed.marker.FeedVO
import java.util.UUID
import javax.inject.Inject


interface FeedVOToUiStateMapper {
    fun voToUiState(vo: FeedVO): FeedUiState
}


class FeedVOToUiStateMapperImpl @Inject constructor() : FeedVOToUiStateMapper {
    override fun voToUiState(vo: FeedVO): FeedUiState {
        return with(vo) {
            when (this) {
                is FeedTransferSearchBarVO -> {
                    FeedTransferSearchBarUiState(
                        id = UUID.randomUUID().toString(),
                        searchKeyword = searchKeyword
                    )
                }

                is FeedMyAccountVO -> {
                    FeedMyAccountUiState(
                        id = id,
                        accountName = accountName,
                        accountNumber = accountNumber,
                        bankName = bankName,
                        iconUrl = iconUrl
                    )
                }

                is FeedMyAccountVOList -> {
                    FeedMyAccountListUiState(
                        id = UUID.randomUUID().toString(),
                        list = list.map { myAccountToUiState(it) }
                    )
                }

                is FeedRecentRecipientVO -> recentRecipientToUiState(this)

                is FeedRecentRecipientVOList -> {
                    FeedRecentRecipientListUiState(
                        id = UUID.randomUUID().toString(),
                        list = list.map { recentRecipientToUiState(it) }
                    )
                }

                else -> NotSupportedUiState
            }
        }
    }
}

private fun myAccountToUiState(
    account: FeedMyAccountVO
): FeedMyAccountUiState {
    return with(account) {
        FeedMyAccountUiState(
            id = id,
            accountName = accountName,
            accountNumber = accountNumber,
            bankName = bankName,
            iconUrl = iconUrl
        )
    }
}

private fun recentRecipientToUiState(
    recent: FeedRecentRecipientVO
): FeedRecentRecipientUiState {
    return when (recent) {
        is FeedRecentRecipientVO.Account -> {
            FeedRecentRecipientUiState.Account(
                id = recent.id,
                name = recent.name,
                iconUrl = recent.iconUrl,
                accountNumber = recent.accountNumber,
                bankName = recent.bankName
            )
        }

        is FeedRecentRecipientVO.Phone -> {
            FeedRecentRecipientUiState.Phone(
                id = recent.id,
                name = recent.name,
                iconUrl = recent.iconUrl,
                phoneNumber = recent.phoneNumber
            )
        }

        FeedRecentRecipientVO.None -> FeedRecentRecipientUiState.None
    }
}
