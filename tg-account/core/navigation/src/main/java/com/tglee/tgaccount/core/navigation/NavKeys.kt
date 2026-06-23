package com.tglee.tgaccount.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 화면 라우트 정의. android_v2 의 @Serializable NavPath 컨셉을 Navigation3 의 NavKey 로 옮긴 형태.
 * @Serializable 은 회전/프로세스 종료 시 백스택 복원(rememberNavBackStack)에 사용된다.
 */

/** 입금처 선택(피드) 화면. */
@Serializable
data object TransferFeedKey : NavKey

/** 송금 화면. 선택한 입금처 정보를 인자로 전달받는다. */
@Serializable
data class TransferSendKey(
    val displayName: String,
    val bankName: String,
    val accountNumber: String,
) : NavKey
