package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedTransferSearchBarVO
import com.tglee.tgaccount.core.feed.mapper.FeedEntityToVOMapper
import com.tglee.tgaccount.core.feed.marker.FeedVO
import com.tglee.tgaccount.data.transferfeed.service.MyAccountService
import com.tglee.tgaccount.data.transferfeed.service.RecentRecipientService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface TransferFeedViewTypeRepository {

    suspend fun refresh()
    fun getMergedViewTypes(): Flow<List<FeedVO>>
}

internal class TransferFeedViewTypeRepositoryImpl @Inject constructor(
    private val myAccountService: MyAccountService,
    private val recentRecipientService: RecentRecipientService,
    private val feedEntityToVOMapper: FeedEntityToVOMapper,
) : TransferFeedViewTypeRepository {

    private val myAccounts = MutableStateFlow(listOf<FeedVO>())
    private val recentRecipients = MutableStateFlow(listOf<FeedVO>())

    private val searchBar = MutableStateFlow(FeedTransferSearchBarVO(""))


    override suspend fun refresh() {
        myAccounts.value = myAccountService.getMyAccounts().map {
            feedEntityToVOMapper.entityToVO(it)
        }

        recentRecipients.value = recentRecipientService.getRecentRecipients().map {
            feedEntityToVOMapper.entityToVO(it)
        }

        searchBar.value = FeedTransferSearchBarVO("")
    }

    override fun getMergedViewTypes(): Flow<List<FeedVO>> {
        return combine(
            searchBar,
            myAccounts,
            recentRecipients,
        ) { searchBar, my, recent ->
            buildList {
                add(searchBar)
                addAll(my)
                addAll(recent)
            }
        }
    }

}
