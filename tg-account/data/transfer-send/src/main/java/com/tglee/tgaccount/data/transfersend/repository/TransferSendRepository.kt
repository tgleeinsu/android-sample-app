package com.tglee.tgaccount.data.transfersend.repository

import com.tglee.tgaccount.data.transfersend.datasource.TransferSendDataSource
import javax.inject.Inject

/**
 * 송금 데이터 레이어의 공개 API. domain 의 use case 가 이 인터페이스에 의존한다.
 * (Google 권장 아키텍처: 인터페이스·구현 모두 데이터 레이어에 위치한다.)
 */
interface TransferSendRepository {
    suspend fun sendMoney(amount: Long)
}

internal class TransferSendRepositoryImpl @Inject constructor(
    private val dataSource: TransferSendDataSource,
) : TransferSendRepository {
    override suspend fun sendMoney(amount: Long) = dataSource.send(amount)
}
