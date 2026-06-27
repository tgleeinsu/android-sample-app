package com.tglee.tgaccount.ui.transfersend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tglee.tgaccount.domain.transfersend.usecase.SendMoneyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransferSendUiState(
    val amount: String = "",
    val isSending: Boolean = false,
) {
    val amountValue: Long get() = amount.toLongOrNull() ?: 0L
    val canSend: Boolean get() = !isSending && amountValue >= 1
}

sealed interface TransferSendEffect {
    data object NavigateBackToFeed : TransferSendEffect
}

@HiltViewModel
class TransferSendViewModel @Inject constructor(
    private val sendMoney: SendMoneyUseCase,
) : ViewModel() {

    // 상태를 ViewModel 에 보관. NavEntry 스코프라 회전 시에도 보존되어 입력값/진행상태가 유지된다.
    private val _uiState = MutableStateFlow(TransferSendUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<TransferSendEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onAmountChange(input: String) {
        if (_uiState.value.isSending) return
        _uiState.value = _uiState.value.copy(amount = input.filter(Char::isDigit))
    }

    fun onClickSend() {
        val current = _uiState.value
        if (!current.canSend) return
        viewModelScope.launch {
            _uiState.value = current.copy(isSending = true)
            sendMoney(current.amountValue) // fake delay
            _uiState.value = _uiState.value.copy(isSending = false)
            _effect.send(TransferSendEffect.NavigateBackToFeed)
        }
    }
}
