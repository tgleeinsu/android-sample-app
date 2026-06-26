package com.tglee.tgaccount.core.common.recent

import kotlinx.serialization.Serializable

/**
 * 입금처 종류. 송금화면 타입별 표시(2-A)와 [방금 송금] 식별에 사용된다.
 * core:common(최하위)에 두어 core:navigation 이 NavKey 필드로 참조해도 순환이 없다.
 */
@Serializable
enum class RecipientType { ACCOUNT, PHONE }
