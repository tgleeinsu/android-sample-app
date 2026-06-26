package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.data.transferfeed.vo.RecentRecipientVO
import com.tglee.tgaccount.data.transferfeed.repository.RecentRecipientRepository
import javax.inject.Inject

interface GetRecentRecipientsUseCase {
    suspend operator fun invoke(): List<RecentRecipientVO>
}

internal class GetRecentRecipientsUseCaseImpl @Inject constructor(
    private val repository: RecentRecipientRepository,
) : GetRecentRecipientsUseCase {
    override suspend fun invoke(): List<RecentRecipientVO> = repository.getRecentRecipients()
}
