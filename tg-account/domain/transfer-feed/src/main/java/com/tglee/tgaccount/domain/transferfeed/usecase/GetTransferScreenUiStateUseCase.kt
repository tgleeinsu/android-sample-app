package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.core.common.result.onFailure
import com.tglee.tgaccount.core.common.result.onSuccess
import com.tglee.tgaccount.core.common.result.runCatchingResult
import com.tglee.tgaccount.core.feed.marker.FeedUiState
import com.tglee.tgaccount.domain.transferfeed.screenuistate.TransferScreenUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface GetTransferScreenUiStateUseCase {
    sealed interface Action {
        object Init : Action
        object Refresh : Action
    }

    sealed interface UiEvent {
        data class ShowErrorToast(
            val error: String
        ): UiEvent
    }
    suspend fun invoke(action: Action)

    fun observe(): Flow<TransferScreenUiState>
    fun observeEvent(): Flow<UiEvent>
}

internal class GetTransferScreenUiStateUseCaseImpl @Inject constructor(
    private val loadTransferFeedUseCase: LoadTransferFeedUseCase,
) : GetTransferScreenUiStateUseCase {

    private data class UseCaseState(
        val isLoading: Boolean = true,
    )

    private val uiState = MutableStateFlow(UseCaseState())
    private val eventChannel = Channel<GetTransferScreenUiStateUseCase.UiEvent>(Channel.BUFFERED)

    override suspend fun invoke(action: GetTransferScreenUiStateUseCase.Action) {
        when (action) {
            GetTransferScreenUiStateUseCase.Action.Init,
            GetTransferScreenUiStateUseCase.Action.Refresh -> load()
        }
    }

    private suspend fun load() {
        uiState.update { it.copy(isLoading = true) }

        runCatchingResult {
            loadTransferFeedUseCase.invoke(LoadTransferFeedUseCase.Action.Load)
        }.onSuccess {
            uiState.update { it.copy(isLoading = false) }
        }.onFailure { throwable ->
            eventChannel.send(GetTransferScreenUiStateUseCase.UiEvent.ShowErrorToast(throwable.message ?: DEFAULT_ERROR_MESSAGE))
        }
    }

    override fun observe(): Flow<TransferScreenUiState> =
        combine(uiState, loadTransferFeedUseCase.observe()) { state, items ->
            state.toScreenState(items)
        }

    override fun observeEvent(): Flow<GetTransferScreenUiStateUseCase.UiEvent> =
        eventChannel.receiveAsFlow()

    private fun UseCaseState.toScreenState(
        items: List<FeedUiState>,
    ): TransferScreenUiState =
        when {
            isLoading -> TransferScreenUiState.Loading()

            items.isEmpty() -> TransferScreenUiState.Loading()

            else -> TransferScreenUiState.Loaded(items = items)
        }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "데이터를 불러오지 못했습니다."
    }
}
