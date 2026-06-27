package com.tglee.tgaccount.ui.transferfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tglee.tgaccount.core.common.recent.JustSentStore
import com.tglee.tgaccount.core.common.recent.RecipientType
import com.tglee.tgaccount.core.common.recent.SentRecipient
import com.tglee.tgaccount.core.feed.FeedItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.FeedSectionHeaderUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.MyAccountItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.MyAccountMoreButtonUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.RecentAccountItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.RecentPhoneItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.SearchBarUiState
import com.tglee.tgaccount.domain.transferfeed.usecase.GetMyAccountsUseCase
import com.tglee.tgaccount.domain.transferfeed.usecase.GetRecentRecipientsUseCase
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVO
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
    private val justSentStore: JustSentStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransferFeedUiState>(TransferFeedUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<TransferFeedEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // 검색어. Route 가 구독해 하이라이트 파라미터로 함께 내려준다.
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private var myAccounts: List<FeedMyAccountVO> = emptyList()
    private var recentRecipients: List<FeedRecentRecipientVO> = emptyList()
    private var expanded: Boolean = false
    private var justSent: SentRecipient? = null
    private var loaded: Boolean = false

    init {
        load()
        // 송금 완료 복귀 시 [방금 송금] 반영 + 검색어 초기화(1-E)를 동시에 처리.
        // 최초 null 방출은 무시한다(아직 송금 없음).
        viewModelScope.launch {
            justSentStore.justSent.collect { sent ->
                if (sent == null) return@collect
                justSent = sent
                _query.value = ""
                if (loaded) rebuild()
            }
        }
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
                loaded = true
                rebuild()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = TransferFeedUiState.Error
                _effect.send(TransferFeedEffect.ShowError(e.message ?: "데이터를 불러오지 못했습니다."))
            }
        }
    }

    fun onToggleMyAccountMore() {
        expanded = !expanded
        rebuild()
    }

    fun onQueryChange(input: String) {
        _query.value = input
        if (loaded) rebuild()
    }

    fun onClearQuery() {
        _query.value = ""
        if (loaded) rebuild()
    }

    /** 두 API 응답 + UI 상태(expanded/query) + [방금 송금] 을 조합해 하나의 피드 리스트를 만든다. */
    private fun rebuild() {
        val q = _query.value.trim()
        val sent = justSent

        // 최근 목록 = justSent(뱃지) ++ baseRecents.filter{ id != justSent.id } (중복 제거, 뱃지 1개)
        // 내 계좌로 송금한 경우에도 SentRecipient(ACCOUNT)를 최근 최상단에 삽입.
        val mergedRecents: List<FeedRecentRecipientVO> =
            if (sent == null) recentRecipients
            else listOf(sent.toRecentVO()) + recentRecipients.filter { it.id != sent.id }

        val items = buildList {
            // ① 검색바
            add(SearchBarUiState(query = _query.value))

            if (q.isBlank()) {
                // ② 내 계좌 (축소 시 showInCollapsed 만 / 펼침 시 전체)
                val visibleAccounts =
                    if (expanded) myAccounts else myAccounts.filter { it.showInCollapsed }
                visibleAccounts.forEach { add(it.toUiState()) }

                // ③ +N개 더보기 / 접기 (숨은 계좌 있을 때만)
                val hiddenCount = myAccounts.count { !it.showInCollapsed }
                if (hiddenCount > 0) {
                    add(MyAccountMoreButtonUiState(expanded = expanded, hiddenCount = hiddenCount))
                }

                // ④ 최근 보낸 계좌 헤더 + 목록
                if (mergedRecents.isNotEmpty()) {
                    add(FeedSectionHeaderUiState(id = "header_recent", title = "최근 보낸 계좌"))
                    mergedRecents.forEach { add(it.toUiState(justSentId = sent?.id)) }
                }
            } else {
                // 검색 모드: 더보기/접기 없음. 매칭된 모든 내 계좌(숨김 포함) → 매칭된 최근 목록.
                myAccounts.filter { it.matches(q) }.forEach { add(it.toUiState()) }

                val matchedRecents = mergedRecents.filter { it.matches(q) }
                if (matchedRecents.isNotEmpty()) {
                    add(FeedSectionHeaderUiState(id = "header_recent", title = "최근 보낸 계좌"))
                    matchedRecents.forEach { add(it.toUiState(justSentId = sent?.id)) }
                }
            }
        }
        _uiState.value = TransferFeedUiState.Loaded(items)
    }
}

/* ----- 매핑/매칭 헬퍼 ----- */

private fun FeedMyAccountVO.toUiState() = MyAccountItemUiState(
    id = id,
    accountName = accountName,
    accountNumber = accountNumber,
    bankName = bankName,
    iconUrl = iconUrl,
)

private fun FeedRecentRecipientVO.toUiState(justSentId: String?): FeedItemUiState = when (this) {
    is FeedRecentRecipientVO.Account -> RecentAccountItemUiState(
        id = id,
        name = name,
        accountNumber = accountNumber,
        bankName = bankName,
        iconUrl = iconUrl,
        justSent = id == justSentId,
    )

    is FeedRecentRecipientVO.Phone -> RecentPhoneItemUiState(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        iconUrl = iconUrl,
        justSent = id == justSentId,
    )
}

/** SentRecipient → 최근 목록 VO. 아이콘은 별도로 보관하지 않으므로 null. */
private fun SentRecipient.toRecentVO(): FeedRecentRecipientVO = when (type) {
    RecipientType.ACCOUNT -> FeedRecentRecipientVO.Account(
        id = id,
        name = name,
        iconUrl = null,
        accountNumber = accountNumber,
        bankName = bankName,
    )

    RecipientType.PHONE -> FeedRecentRecipientVO.Phone(
        id = id,
        name = name,
        iconUrl = null,
        phoneNumber = phoneNumber,
    )
}

/** 계좌 매칭: 이름/은행/계좌번호 대소문자 무시 contains. */
private fun FeedMyAccountVO.matches(q: String): Boolean =
    accountName.contains(q, ignoreCase = true) ||
        bankName.contains(q, ignoreCase = true) ||
        accountNumber.contains(q, ignoreCase = true)

/** 최근 매칭: account 는 이름/은행/계좌번호, phone 은 이름/전화번호. */
private fun FeedRecentRecipientVO.matches(q: String): Boolean = when (this) {
    is FeedRecentRecipientVO.Account ->
        name.contains(q, ignoreCase = true) ||
            bankName.contains(q, ignoreCase = true) ||
            accountNumber.contains(q, ignoreCase = true)

    is FeedRecentRecipientVO.Phone ->
        name.contains(q, ignoreCase = true) ||
            phoneNumber.contains(q, ignoreCase = true)
}
