package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.data.transferfeed.model.MyAccountVO
import com.tglee.tgaccount.data.transferfeed.model.RecentRecipientVO

/**
 * 데이터 레이어의 공개 API(repository). 데이터 소스를 감싸 앱의 single source of truth 역할을 한다.
 * (Google 권장 아키텍처: repository 인터페이스는 데이터 레이어에 위치한다.)
 */
interface MyAccountRepository {
    suspend fun getMyAccounts(): List<MyAccountVO>
}

interface RecentRecipientRepository {
    suspend fun getRecentRecipients(): List<RecentRecipientVO>
}
