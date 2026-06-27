package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.data.transferfeed.vo.FeedVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVOList
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVOList
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
        recentRecipients: FeedRecentRecipientVOList,
    ): List<FeedVO> = buildList {
        addAll(myAccounts.list)
        addAll(recentRecipients.list)
    }
}
