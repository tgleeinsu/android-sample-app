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
import com.tglee.tgaccount.ui.transferfeed.vm.TransferScreenViewModel
import kotlin.io.path.Path

@Stable
internal data class TransferScreenState(
    val screenUiState: TransferScreenUiState
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

    return remember(
        viewModel,
        context,
        screenUiState,
    ) {
        TransferScreenState(
            screenUiState = screenUiState,
        )
    }
}