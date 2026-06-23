package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.data.transferfeed.mapper.toVO
import com.tglee.tgaccount.data.transferfeed.service.MyAccountService
import com.tglee.tgaccount.data.transferfeed.service.RecentRecipientService
import com.tglee.tgaccount.domain.transferfeed.repository.MyAccountRepository
import com.tglee.tgaccount.domain.transferfeed.repository.RecentRecipientRepository
import com.tglee.tgaccount.domain.transferfeed.vo.MyAccountVO
import com.tglee.tgaccount.domain.transferfeed.vo.RecentRecipientVO
import javax.inject.Inject

internal class MyAccountRepositoryImpl @Inject constructor(
    private val service: MyAccountService,
) : MyAccountRepository {
    override suspend fun getMyAccounts(): List<MyAccountVO> =
        service.getMyAccounts().map { it.toVO() }
}

internal class RecentRecipientRepositoryImpl @Inject constructor(
    private val service: RecentRecipientService,
) : RecentRecipientRepository {
    override suspend fun getRecentRecipients(): List<RecentRecipientVO> =
        service.getRecentRecipients().mapNotNull { it.toVO() }
}
