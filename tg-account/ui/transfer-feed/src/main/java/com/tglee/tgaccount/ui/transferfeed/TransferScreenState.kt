package com.tglee.tgaccount.ui.transferfeed

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tglee.tgaccount.core.feed.marker.FeedItemStateParam
import com.tglee.tgaccount.core.navigation.TransferSendKey
import com.tglee.tgaccount.domain.transferfeed.screenuistate.TransferScreenUiState
import com.tglee.tgaccount.domain.transferfeed.usecase.GetTransferScreenUiStateUseCase
import com.tglee.tgaccount.ui.transferfeed.feeditem.event.TransferFeedEvent
import com.tglee.tgaccount.ui.transferfeed.vm.TransferScreenViewModel

@Stable
internal data class TransferScreenState(
    val screenUiState: TransferScreenUiState,
    val itemStateParam: FeedItemStateParam,
)


@Composable
internal fun rememberTransferScreenState(
    onSelectRecipient: (TransferSendKey) -> Unit,
    viewModel: TransferScreenViewModel = hiltViewModel(),
): TransferScreenState {
    val context = LocalContext.current

    val screenUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect {
            when (it) {
                is GetTransferScreenUiStateUseCase.UiEvent.ShowErrorToast -> {
                    Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val itemStateParam = remember {
        FeedItemStateParam(
            onEvent = { event ->
                when (event) {
                    is TransferFeedEvent.SelectMyAccount ->
                        onSelectRecipient(
                            TransferSendKey(recipientId = event.uiState.id, name = event.uiState.accountName),
                        )

                    is TransferFeedEvent.SelectRecentAccount ->
                        onSelectRecipient(
                            TransferSendKey(recipientId = event.uiState.id, name = event.uiState.name),
                        )

                    is TransferFeedEvent.SelectRecentPhone ->
                        onSelectRecipient(
                            TransferSendKey(recipientId = event.uiState.id, name = event.uiState.name),
                        )

                    TransferFeedEvent.ToggleMyAccountMore -> Unit // TODO 조립 UseCase
                    is TransferFeedEvent.ChangeSearchKeyword -> Unit // TODO 조립 UseCase
                    TransferFeedEvent.ClearSearchKeyword -> Unit // TODO 조립 UseCase
                    else -> Unit // 이 화면이 모르는 이벤트(타 모듈 아이템 등)는 무시
                }
            },
        )
    }

    return remember(
        viewModel,
        context,
        screenUiState,
        itemStateParam,
    ) {
        TransferScreenState(
            screenUiState = screenUiState,
            itemStateParam = itemStateParam,
        )
    }
}