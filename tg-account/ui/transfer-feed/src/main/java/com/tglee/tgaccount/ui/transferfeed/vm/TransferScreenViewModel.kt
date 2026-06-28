package com.tglee.tgaccount.ui.transferfeed.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tglee.tgaccount.domain.transferfeed.screenuistate.TransferScreenUiState
import com.tglee.tgaccount.domain.transferfeed.usecase.GetTransferScreenUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class TransferScreenViewModel @Inject constructor(
    private val getTransferScreenUiStateUseCase: GetTransferScreenUiStateUseCase
) : ViewModel() {

    val uiState = getTransferScreenUiStateUseCase.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TransferScreenUiState.Loading(),
        )

    val eventChannel = getTransferScreenUiStateUseCase.observeEvent()
}