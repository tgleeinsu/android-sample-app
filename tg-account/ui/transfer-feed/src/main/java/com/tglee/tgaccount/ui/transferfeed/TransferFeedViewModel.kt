package com.tglee.tgaccount.ui.transferfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tglee.tgaccount.core.feed.FeedItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.FeedSectionHeaderUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.MyAccountItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.MyAccountMoreButtonUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.RecentAccountItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.RecentPhoneItemUiState
import com.tglee.tgaccount.domain.transferfeed.uistate.SearchBarUiState
import com.tglee.tgaccount.domain.transferfeed.usecase.GetMyAccountsUseCase
import com.tglee.tgaccount.domain.transferfeed.usecase.GetRecentRecipientsUseCase
import com.tglee.tgaccount.domain.transferfeed.vo.MyAccountVO
import com.tglee.tgaccount.domain.transferfeed.vo.RecentRecipientVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TransferFeedUiState {
    data object Loading : TransferFeedUiState
    data class Loaded(val items: List<FeedItemUiState>) : TransferFeedUiState
    data object Error : TransferFeedUiState
}

sealed interface TransferFeedEffect {
    data class ShowError(val message: String) : TransferFeedEffect
}

@HiltViewModel
class TransferFeedViewModel @Inject constructor(
    private val getMyAccounts: GetMyAccountsUseCase,
    private val getRecentRecipients: GetRecentRecipientsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransferFeedUiState>(TransferFeedUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<TransferFeedEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var myAccounts: List<MyAccountVO> = emptyList()
    private var recentRecipients: List<RecentRecipientVO> = emptyList()
    private var expanded: Boolean = false

    // init 에서 1회만 로드. ViewModel 은 NavEntry 스코프라 회전 시에도 살아남아 API 가 재호출되지 않는다.
    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = TransferFeedUiState.Loading
            try {
                coroutineScope {
                    val accountsDeferred = async { getMyAccounts() }
                    val recentsDeferred = async { getRecentRecipients() }
                    myAccounts = accountsDeferred.await()
                    recentRecipients = recentsDeferred.await()
                }
                rebuild()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 실패 시 화면은 비우고(Error) 토스트 이벤트 전송
                _uiState.value = TransferFeedUiState.Error
                _effect.send(TransferFeedEffect.ShowError(e.message ?: "데이터를 불러오지 못했습니다."))
            }
        }
    }

    fun onToggleMyAccountMore() {
        expanded = !expanded
        rebuild()
    }

    /** 두 API 응답 + UI 상태(expanded)를 조합해 하나의 피드 리스트를 만든다. */
    private fun rebuild() {
        val items = buildList {
            // ① 검색바
            add(SearchBarUiState())

            // ② 내 계좌 목록 (축소 시 visibleWhenCollapsed 만)
            val visibleAccounts =
                if (expanded) myAccounts else myAccounts.filter { it.visibleWhenCollapsed }
            visibleAccounts.forEach { acc ->
                add(
                    MyAccountItemUiState(
                        id = acc.id,
                        accountName = acc.accountName,
                        accountNumber = acc.accountNumber,
                        bankName = acc.bankName,
                        iconUrl = acc.iconUrl,
                    ),
                )
            }

            // ③ 더보기/접기 버튼 (숨겨진 계좌가 있을 때만)
            val hasCollapsible = myAccounts.any { !it.visibleWhenCollapsed }
            if (hasCollapsible) {
                add(MyAccountMoreButtonUiState(expanded = expanded))
            }

            // ④ 최근 보낸 계좌 목록
            if (recentRecipients.isNotEmpty()) {
                add(FeedSectionHeaderUiState(id = "header_recent", title = "최근 보낸 계좌"))
                recentRecipients.forEach { recipient ->
                    when (recipient) {
                        is RecentRecipientVO.Account -> add(
                            RecentAccountItemUiState(
                                id = recipient.id,
                                name = recipient.name,
                                accountNumber = recipient.accountNumber,
                                bankName = recipient.bankName,
                                iconUrl = recipient.iconUrl,
                            ),
                        )

                        is RecentRecipientVO.Phone -> add(
                            RecentPhoneItemUiState(
                                id = recipient.id,
                                name = recipient.name,
                                phoneNumber = recipient.phoneNumber,
                                iconUrl = recipient.iconUrl,
                            ),
                        )
                    }
                }
            }
        }
        _uiState.value = TransferFeedUiState.Loaded(items)
    }
}
