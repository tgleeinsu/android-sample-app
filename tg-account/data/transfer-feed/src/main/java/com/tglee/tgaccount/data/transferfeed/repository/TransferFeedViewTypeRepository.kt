package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedMyAccountMoreButtonVO
import com.tglee.tgaccount.core.feed.feedmodel.vo.FeedSectionHeaderVO
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


    override suspend fun refresh() {
        myAccounts.value = myAccountService.getMyAccounts().map {
            feedEntityToVOMapper.entityToVO(it)
        }

        recentRecipients.value = recentRecipientService.getRecentRecipients().map {
            feedEntityToVOMapper.entityToVO(it)
        }
    }

    /**
     * 본래 서버 드리븐으로 내려주던 feed response를 강제 구현.
     * searchBar·더보기·섹션헤더는 서버 기본값에 해당하는 정적 VO 라 flow 없이 그대로 끼워넣는다.
     * (펼침·검색·조건부 노출 등 동적 조립은 상위 UseCase 담당)
     * */
    override fun getMergedViewTypes(): Flow<List<FeedVO>> {
        return combine(
            myAccounts,
            recentRecipients,
        ) { my, recent ->
            buildList {
                add(FeedTransferSearchBarVO(""))
                addAll(my)
                add(FeedMyAccountMoreButtonVO(expanded = false, hiddenCount = 0))
                add(FeedSectionHeaderVO(title = ""))
                addAll(recent)
            }
        }
    }

}
