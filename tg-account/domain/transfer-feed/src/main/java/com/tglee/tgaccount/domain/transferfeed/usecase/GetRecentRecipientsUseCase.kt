package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVO
import com.tglee.tgaccount.data.transferfeed.repository.RecentRecipientRepository
import javax.inject.Inject

interface GetRecentRecipientsUseCase {
    suspend operator fun invoke(): List<FeedRecentRecipientVO>
}

internal class GetRecentRecipientsUseCaseImpl @Inject constructor(
    private val repository: RecentRecipientRepository,
) : GetRecentRecipientsUseCase {
    override suspend fun invoke(): List<FeedRecentRecipientVO> = repository.getRecentRecipients()
}
