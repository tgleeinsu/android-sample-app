package com.tglee.tgaccount.core.common.recent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** 방금 송금한 입금처. 송금(ui:transfer-send)이 쓰고 피드(ui:transfer-feed)가 읽는다. */
data class SentRecipient(
    val id: String,
    val type: RecipientType,
    val name: String,
    val bankName: String = "",      // ACCOUNT 전용
    val accountNumber: String = "", // ACCOUNT 전용
    val phoneNumber: String = "",   // PHONE 전용
)

/**
 * [방금 송금] 공유 스토어. 모든 송금 완료 시 마지막 입금처 1개를 in-memory 로 보관한다.
 * @Singleton 이라 앱 종료까지 유지되며, 가장 최근 1건만 뱃지 대상이 된다.
 * (참조 아키텍처의 StoryEventProvider/Store 패턴 대응.)
 */
@Singleton
class JustSentStore @Inject constructor() {
    private val _justSent = MutableStateFlow<SentRecipient?>(null)
    val justSent: StateFlow<SentRecipient?> = _justSent.asStateFlow()

    fun markSent(recipient: SentRecipient) {
        _justSent.value = recipient
    }
}
