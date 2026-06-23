package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.domain.transferfeed.repository.RecentRecipientRepository
import com.tglee.tgaccount.domain.transferfeed.vo.RecentRecipientVO
import javax.inject.Inject

interface GetRecentRecipientsUseCase {
    suspend operator fun invoke(): List<RecentRecipientVO>
}

internal class GetRecentRecipientsUseCaseImpl @Inject constructor(
    private val repository: RecentRecipientRepository,
) : GetRecentRecipientsUseCase {
    override suspend fun invoke(): List<RecentRecipientVO> = repository.getRecentRecipients()
}
