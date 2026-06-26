package com.tglee.tgaccount.data.transfersend.datasource

import kotlinx.coroutines.delay
import javax.inject.Inject

/** 송금 데이터 소스. 실제 통신 대신 10초 delay 로 송금을 흉내내는 fake 구현. */
interface TransferSendDataSource {
    suspend fun send(amount: Long)
}

internal class FakeTransferSendDataSource @Inject constructor() : TransferSendDataSource {
    override suspend fun send(amount: Long) {
        delay(SEND_DELAY_MS)
    }

    companion object {
        private const val SEND_DELAY_MS = 10_000L
    }
}
