package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.data.transferfeed.repository.TransferFeedViewTypeRepository
import com.tglee.tgaccount.data.transferfeed.vo.FeedTransferFeedVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedVO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoadTransferFeedUseCase {
    fun observe(): Flow<List<FeedVO>>

}


internal class LoadTransferFeedUseCaseImpl @Inject constructor(
    val transferFeedViewTypeRepository: TransferFeedViewTypeRepository
) : LoadTransferFeedUseCase {


    override fun observe(): Flow<List<FeedVO>> {
        return transferFeedViewTypeRepository.getMergedViewTypes()
    }
}