package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.core.feed.FeedVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountMoreButtonVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVOList
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVOList
import com.tglee.tgaccount.data.transferfeed.vo.FeedSearchBarVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface TransferFeedViewTypeRepository {
    fun getMergedViewTypes(): Flow<List<FeedVO>>
}

internal class TransferFeedViewTypeRepositoryImpl @Inject constructor(
    private val myAccountRepo: MyAccountRepository,
    private val recentRecipientRepo: RecentRecipientRepository,
) : TransferFeedViewTypeRepository {

    override fun getMergedViewTypes(): Flow<List<FeedVO>> =
        combine(
            myAccountRepo.myAccounts,
            recentRecipientRepo.recentRecipients,
        ) { myAccounts, recent ->
            makeTransferFeedViewTypeList(myAccounts, recent)
        }

    private fun makeTransferFeedViewTypeList(
        myAccounts: FeedMyAccountVOList,
        recentRecipients: FeedRecentRecipientVOList
    ): List<FeedVO> {
        return buildList {
            add(FeedSearchBarVO())

            if (myAccounts.list.isNotEmpty()) {
                myAccounts.list.filter { myAccount ->
                    myAccount.showInCollapsed
                }.forEach { visibleAccount ->
                    add(visibleAccount)
                }

                myAccounts.list.firstOrNull { myAccount ->
                    myAccount.showInCollapsed.not()
                }?.let {
                    add(FeedMyAccountMoreButtonVO())
                }
            }

            if (recentRecipients.list.isNotEmpty()) {
                recentRecipients.list.forEach { recentRecipient ->
                    add(recentRecipient)
                }
            }
        }
    }
}
