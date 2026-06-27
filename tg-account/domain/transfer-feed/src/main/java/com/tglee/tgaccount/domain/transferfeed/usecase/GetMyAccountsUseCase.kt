package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVO
import com.tglee.tgaccount.data.transferfeed.repository.MyAccountRepository
import javax.inject.Inject

interface GetMyAccountsUseCase {
    suspend operator fun invoke(): List<FeedMyAccountVO>
}

internal class GetMyAccountsUseCaseImpl @Inject constructor(
    private val repository: MyAccountRepository,
) : GetMyAccountsUseCase {
    override suspend fun invoke(): List<FeedMyAccountVO> = repository.getMyAccounts()
}
