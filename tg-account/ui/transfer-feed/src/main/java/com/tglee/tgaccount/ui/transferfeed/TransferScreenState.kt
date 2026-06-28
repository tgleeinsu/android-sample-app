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
import com.tglee.tgaccount.core.navigation.TransferSendKey
import com.tglee.tgaccount.domain.transferfeed.screenuistate.TransferScreenUiState
import com.tglee.tgaccount.domain.transferfeed.usecase.GetTransferScreenUiStateUseCase
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.TransferFeedStateParam
import com.tglee.tgaccount.ui.transferfeed.vm.TransferScreenViewModel

@Stable
internal data class TransferScreenState(
    val screenUiState: TransferScreenUiState,
    val itemStateParam: TransferFeedStateParam,
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

    val itemStateParam = remember(onSelectRecipient) {
        TransferFeedStateParam(
            searchKeyword = "", // TODO
            onChangeSearchKeyword = {}, // TODO
            onClearSearchKeyword = {}, // TODO
            onToggleMyAccountMore = {}, // TODO
            onSelectMyAccount = { acc ->
                onSelectRecipient(
                    TransferSendKey(
                        recipientId = acc.id,
                        name = acc.accountName,
                    ),
                )
            },
            onSelectRecentAccount = { rcp ->
                onSelectRecipient(
                    TransferSendKey(
                        recipientId = rcp.id,
                        name = rcp.name,
                    ),
                )
            },
            onSelectRecentPhone = { rcp ->
                onSelectRecipient(
                    TransferSendKey(
                        recipientId = rcp.id,
                        name = rcp.name,
                    ),
                )
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