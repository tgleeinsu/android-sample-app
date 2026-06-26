package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.data.transferfeed.mapper.toVO
import com.tglee.tgaccount.data.transferfeed.service.RecentRecipientService
import com.tglee.tgaccount.data.transferfeed.vo.RecentRecipientVO
import javax.inject.Inject

interface RecentRecipientRepository {
    suspend fun getRecentRecipients(): List<RecentRecipientVO>
}


internal class RecentRecipientRepositoryImpl @Inject constructor(
    private val service: RecentRecipientService,
) : RecentRecipientRepository {
    override suspend fun getRecentRecipients(): List<RecentRecipientVO> =
        service.getRecentRecipients().map { it.toVO() }
}
