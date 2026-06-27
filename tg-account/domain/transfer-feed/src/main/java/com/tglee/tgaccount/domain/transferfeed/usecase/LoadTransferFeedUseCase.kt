package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.core.feed.mapper.FeedVOToUiStateMapper
import com.tglee.tgaccount.core.feed.marker.FeedUiState
import com.tglee.tgaccount.data.transferfeed.repository.TransferFeedViewTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LoadTransferFeedUseCase {
    sealed interface Action {
        object Load: Action
        object Refresh: Action
    }

    suspend fun invoke(action: Action)
    fun observe(): Flow<List<FeedUiState>>

}


internal class LoadTransferFeedUseCaseImpl @Inject constructor(
    private val transferFeedViewTypeRepository: TransferFeedViewTypeRepository,
    private val feedVOToUiStateMapper: FeedVOToUiStateMapper
) : LoadTransferFeedUseCase {

    override suspend fun invoke(action: LoadTransferFeedUseCase.Action) {
        when(action) {
            LoadTransferFeedUseCase.Action.Load -> transferFeedViewTypeRepository.refreshAccounts()
            LoadTransferFeedUseCase.Action.Refresh -> transferFeedViewTypeRepository.refreshAccounts()
        }
    }


    override fun observe(): Flow<List<FeedUiState>> {
        return transferFeedViewTypeRepository.getMergedViewTypes().map {
            it.map { feed ->
                feedVOToUiStateMapper.voToUiState(feed)
            }
        }
    }
}