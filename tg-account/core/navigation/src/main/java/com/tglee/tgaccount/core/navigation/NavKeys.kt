package com.tglee.tgaccount.core.navigation

import androidx.navigation3.runtime.NavKey
import com.tglee.tgaccount.core.common.recent.RecipientType
import kotlinx.serialization.Serializable

/**
 * 화면 라우트 정의. @Serializable NavKey 로 타입 안전 라우팅을 구성한다.
 * @Serializable 은 회전/프로세스 종료 시 백스택 복원(rememberNavBackStack)에 사용된다.
 */

/** 입금처 선택(피드) 화면. */
@Serializable
data object TransferFeedKey : NavKey

/** 송금 화면. 선택한 입금처 정보를 인자로 전달받는다. 타입별 표시·[방금 송금] 식별에 필드를 확장. */
@Serializable
data class TransferSendKey(
    val recipientId: String,
    val type: RecipientType,
    val name: String,
    val bankName: String = "",      // ACCOUNT 전용
    val accountNumber: String = "", // ACCOUNT 전용
    val phoneNumber: String = "",   // PHONE 전용
) : NavKey
