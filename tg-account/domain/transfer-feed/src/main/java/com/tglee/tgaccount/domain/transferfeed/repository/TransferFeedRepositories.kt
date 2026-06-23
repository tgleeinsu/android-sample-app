package com.tglee.tgaccount.domain.transferfeed.repository

import com.tglee.tgaccount.domain.transferfeed.vo.MyAccountVO
import com.tglee.tgaccount.domain.transferfeed.vo.RecentRecipientVO

interface MyAccountRepository {
    suspend fun getMyAccounts(): List<MyAccountVO>
}

interface RecentRecipientRepository {
    suspend fun getRecentRecipients(): List<RecentRecipientVO>
}
