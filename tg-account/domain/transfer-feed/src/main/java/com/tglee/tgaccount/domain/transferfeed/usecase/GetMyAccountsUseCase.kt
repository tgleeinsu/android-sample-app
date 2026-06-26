package com.tglee.tgaccount.domain.transferfeed.usecase

import com.tglee.tgaccount.data.transferfeed.vo.MyAccountVO
import com.tglee.tgaccount.data.transferfeed.repository.MyAccountRepository
import javax.inject.Inject

interface GetMyAccountsUseCase {
    suspend operator fun invoke(): List<MyAccountVO>
}

internal class GetMyAccountsUseCaseImpl @Inject constructor(
    private val repository: MyAccountRepository,
) : GetMyAccountsUseCase {
    override suspend fun invoke(): List<MyAccountVO> = repository.getMyAccounts()
}
