package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.core.common.di.ApplicationScope
import com.tglee.tgaccount.data.transferfeed.mapper.toVO
import com.tglee.tgaccount.data.transferfeed.service.RecentRecipientService
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVOList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface RecentRecipientRepository {
    suspend fun getRecentRecipients(): FeedRecentRecipientVOList
    val recentRecipients: StateFlow<FeedRecentRecipientVOList>
}

internal class RecentRecipientRepositoryImpl @Inject constructor(
    private val service: RecentRecipientService,
    @ApplicationScope private val scope: CoroutineScope,
) : RecentRecipientRepository {

    private val _recentRecipients = MutableStateFlow(FeedRecentRecipientVOList(emptyList()))
    override val recentRecipients: StateFlow<FeedRecentRecipientVOList> = _recentRecipients.asStateFlow()

    init {
        scope.launch { _recentRecipients.value = getRecentRecipients() }
    }

    override suspend fun getRecentRecipients(): FeedRecentRecipientVOList =
        service.getRecentRecipients().toVO()
}
