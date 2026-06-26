package com.tglee.tgaccount.domain.transfersend.usecase

import com.tglee.tgaccount.data.transfersend.repository.TransferSendRepository
import javax.inject.Inject

/**
 * 송금 use case. 데이터 레이어의 [TransferSendRepository] 에 위임한다 (domain → data).
 */
interface SendMoneyUseCase {
    suspend operator fun invoke(amount: Long)
}

internal class SendMoneyUseCaseImpl @Inject constructor(
    private val repository: TransferSendRepository,
) : SendMoneyUseCase {
    override suspend fun invoke(amount: Long) = repository.sendMoney(amount)
}
