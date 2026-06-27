package com.tglee.tgaccount.data.transferfeed.service

import com.tglee.tgaccount.core.common.asset.AssetJsonLoader
import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedMyAccountEntity
import com.tglee.tgaccount.core.feed.feedmodel.entity.FeedRecentRecipientEntity
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/** 실패 시뮬레이션 토글. 에러 토스트/빈 화면 동작을 수동 검증할 때 사용. */
@Singleton
class MockFailureSwitch @Inject constructor() {
    @Volatile
    var failMyAccounts: Boolean = false

    @Volatile
    var failRecentRecipients: Boolean = false
}

interface MyAccountService {
    suspend fun getMyAccounts(): List<FeedMyAccountEntity>
}

/** 실제 통신 대신 로컬 assets json 을 반환하는 fake 구현. */
internal class FakeMyAccountService @Inject constructor(
    private val loader: AssetJsonLoader,
    private val failureSwitch: MockFailureSwitch,
) : MyAccountService {
    override suspend fun getMyAccounts(): List<FeedMyAccountEntity> {
        delay(MOCK_LATENCY_MS)
        if (failureSwitch.failMyAccounts) error("Mock: 내 계좌 목록 조회 실패")
        return loader.load("my_accounts.json")
    }
}

interface RecentRecipientService {
    suspend fun getRecentRecipients(): List<FeedRecentRecipientEntity>
}

/** 실제 통신 대신 로컬 assets json 을 반환하는 fake 구현. */
internal class FakeRecentRecipientService @Inject constructor(
    private val loader: AssetJsonLoader,
    private val failureSwitch: MockFailureSwitch,
) : RecentRecipientService {
    override suspend fun getRecentRecipients(): List<FeedRecentRecipientEntity> {
        delay(MOCK_LATENCY_MS)
        if (failureSwitch.failRecentRecipients) error("Mock: 최근 보낸 계좌 조회 실패")
        return loader.load("recent_recipients.json")
    }
}

private const val MOCK_LATENCY_MS = 400L
