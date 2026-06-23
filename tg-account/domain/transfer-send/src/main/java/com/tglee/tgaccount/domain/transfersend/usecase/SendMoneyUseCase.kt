package com.tglee.tgaccount.domain.transfersend.usecase

import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * fake 송금. 별도 API 호출 없이 10초 delay 후 완료된 것으로 간주한다.
 */
interface SendMoneyUseCase {
    suspend operator fun invoke(amount: Long)
}

internal class SendMoneyUseCaseImpl @Inject constructor() : SendMoneyUseCase {
    override suspend fun invoke(amount: Long) {
        delay(SEND_DELAY_MS)
    }

    companion object {
        private const val SEND_DELAY_MS = 10_000L
    }
}
