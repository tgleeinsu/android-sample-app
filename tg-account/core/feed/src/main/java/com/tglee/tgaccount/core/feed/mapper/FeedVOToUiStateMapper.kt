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
import javax.inject.Inject

private const val ID_SEARCH_BAR = "feed_transfer_search_bar"
private const val ID_MY_ACCOUNT_LIST = "feed_my_account_list"
private const val ID_RECENT_RECIPIENT_LIST = "feed_recent_recipient_list"


interface FeedVOToUiStateMapper {
    fun voToUiState(vo: FeedVO): FeedUiState
}


class FeedVOToUiStateMapperImpl @Inject constructor() : FeedVOToUiStateMapper {
    override fun voToUiState(vo: FeedVO): FeedUiState {
        return with(vo) {
            when (this) {
                is FeedTransferSearchBarVO -> {
                    FeedTransferSearchBarUiState(
                        id = ID_SEARCH_BAR,
                        searchKeyword = searchKeyword
                    )
                }

                is FeedMyAccountVO -> myAccountToUiState(this)

                is FeedMyAccountVOList -> {
                    FeedMyAccountListUiState(
                        id = ID_MY_ACCOUNT_LIST,
                        list = list.map { myAccountToUiState(it) }
                    )
                }

                is FeedRecentRecipientVO -> recentRecipientToUiState(this)

                is FeedRecentRecipientVOList -> {
                    FeedRecentRecipientListUiState(
                        id = ID_RECENT_RECIPIENT_LIST,
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
