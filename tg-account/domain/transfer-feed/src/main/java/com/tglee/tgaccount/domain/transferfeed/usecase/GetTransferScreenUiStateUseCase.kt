package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.core.common.result.onFailure
import com.tglee.tgaccount.core.common.result.onSuccess
import com.tglee.tgaccount.core.common.result.runCatchingResult
import com.tglee.tgaccount.core.feed.marker.FeedUiState
import com.tglee.tgaccount.domain.transferfeed.usecase.GetTransferScreenUiStateUseCase.TransferScreenUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface GetTransferScreenUiStateUseCase {
    sealed interface Action {
        object Init : Action
        object Refresh : Action
    }

    sealed interface TransferScreenUiState {
        val isLoading: Boolean

        data class Loading(
            override val isLoading: Boolean = true
        ) : TransferScreenUiState


        data class Loaded(
            override val isLoading: Boolean = false,
            val items: List<FeedUiState>,
        ) : TransferScreenUiState

        data class Error(
            override val isLoading: Boolean = false,
            val error: String
        ) : TransferScreenUiState
    }

    suspend fun invoke(action: Action)

    fun observe(): Flow<TransferScreenUiState>
}

internal class GetTransferScreenUiStateUseCaseImpl @Inject constructor(
    private val loadTransferFeedUseCase: LoadTransferFeedUseCase,
) : GetTransferScreenUiStateUseCase {

    private data class UseCaseState(
        val isLoading: Boolean = true,
        val error: Throwable? = null,
    )

    private val uiState = MutableStateFlow(UseCaseState())

    override suspend fun invoke(action: GetTransferScreenUiStateUseCase.Action) {
        when (action) {
            GetTransferScreenUiStateUseCase.Action.Init,
            GetTransferScreenUiStateUseCase.Action.Refresh -> load()
        }
    }

    private suspend fun load() {
        uiState.update { it.copy(isLoading = true, error = null) }

        runCatchingResult {
            loadTransferFeedUseCase.invoke(LoadTransferFeedUseCase.Action.Load)
        }.onSuccess {
            uiState.update { it.copy(isLoading = false) }
        }.onFailure { throwable ->
            uiState.update { it.copy(isLoading = false, error = throwable) }
        }
    }

    override fun observe(): Flow<TransferScreenUiState> =
        combine(uiState, loadTransferFeedUseCase.observe()) { state, items ->
            state.toScreenState(items)
        }

    private fun UseCaseState.toScreenState(
        items: List<FeedUiState>,
    ): TransferScreenUiState =
        when {
            isLoading -> TransferScreenUiState.Loading()

            error != null -> TransferScreenUiState.Error(
                error = error.message ?: DEFAULT_ERROR_MESSAGE
            )

            items.isEmpty() -> TransferScreenUiState.Loading()

            else -> TransferScreenUiState.Loaded(items = items)
        }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "데이터를 불러오지 못했습니다."
    }
}
