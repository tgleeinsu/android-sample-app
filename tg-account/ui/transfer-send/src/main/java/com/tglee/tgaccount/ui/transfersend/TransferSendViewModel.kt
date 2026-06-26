package com.tglee.tgaccount.ui.transfersend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tglee.tgaccount.core.common.recent.JustSentStore
import com.tglee.tgaccount.core.common.recent.SentRecipient
import com.tglee.tgaccount.domain.transfersend.usecase.SendMoneyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 송금 금액 상한(200만원). */
const val MAX_AMOUNT: Long = 2_000_000L

data class TransferSendUiState(
    val amount: String = "",
    val isSending: Boolean = false,
    val showMaxDialog: Boolean = false,
) {
    val amountValue: Long get() = amount.toLongOrNull() ?: 0L
    val canSend: Boolean get() = !isSending && amountValue in 1..MAX_AMOUNT
}

sealed interface TransferSendEffect {
    data object NavigateBackToFeed : TransferSendEffect
}

@HiltViewModel
class TransferSendViewModel @Inject constructor(
    private val sendMoney: SendMoneyUseCase,
    private val justSentStore: JustSentStore,
) : ViewModel() {

    // 상태를 ViewModel 에 보관. NavEntry 스코프라 회전 시에도 보존되어 입력값/진행상태가 유지된다.
    private val _uiState = MutableStateFlow(TransferSendUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<TransferSendEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onAmountChange(input: String) {
        if (_uiState.value.isSending) return
        val digits = input.filter(Char::isDigit)
        val value = digits.toLongOrNull() ?: 0L
        if (value > MAX_AMOUNT) {
            // 200만원 초과 입력은 거부(이전 값 유지) + 안내 다이얼로그.
            _uiState.value = _uiState.value.copy(showMaxDialog = true)
            return
        }
        _uiState.value = _uiState.value.copy(amount = digits)
    }

    fun onDismissMaxDialog() {
        _uiState.value = _uiState.value.copy(showMaxDialog = false)
    }

    fun onClickSend(recipient: SentRecipient) {
        val current = _uiState.value
        if (!current.canSend) return
        viewModelScope.launch {
            _uiState.value = current.copy(isSending = true)
            sendMoney(current.amountValue) // 10초 delay
            justSentStore.markSent(recipient)
            _uiState.value = _uiState.value.copy(isSending = false)
            _effect.send(TransferSendEffect.NavigateBackToFeed)
        }
    }
}
