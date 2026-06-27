package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedTransferFeedVO
import com.tglee.tgaccount.domain.transferfeed.usecase.LoadTransferFeedUseCase.Action
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface LoadTransferFeedUseCase {

    data class Action(
        val searchKeyword: String
    )

    suspend operator fun invoke(action: Action): FeedTransferFeedVO
}


internal class LoadTransferFeedUseCaseImpl(
    private val getMyAccounts: GetMyAccountsUseCase,
    private val getRecentRecipients: GetRecentRecipientsUseCase,
) : LoadTransferFeedUseCase {
    override suspend fun invoke(action: Action): FeedTransferFeedVO {

        var myAccounts: List<FeedMyAccountVO> = emptyList()
        var recentRecipients: List<FeedRecentRecipientVO> = emptyList()

        coroutineScope {
            val accountsDeferred = async { getMyAccounts() }
            val recentsDeferred = async { getRecentRecipients() }
            myAccounts = accountsDeferred.await()
            recentRecipients = recentsDeferred.await()
        }

        val items = buildList {
            if (action.searchKeyword.isBlank()) {
                val visibleAccounts = myAccounts.map {
                    add(it.showInCollapsed)
                }
            } else {

            }
        }

    }

    private fun mergeViewTypes() {

    }
}