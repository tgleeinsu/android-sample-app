package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.core.common.di.ApplicationScope
import com.tglee.tgaccount.data.transferfeed.mapper.toVO
import com.tglee.tgaccount.data.transferfeed.service.MyAccountService
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVOList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MyAccountRepository {
    suspend fun getMyAccounts(): FeedMyAccountVOList
    val myAccounts: StateFlow<FeedMyAccountVOList>
}

internal class MyAccountRepositoryImpl @Inject constructor(
    private val service: MyAccountService,
    @ApplicationScope private val scope: CoroutineScope,
) : MyAccountRepository {

    private val _myAccounts = MutableStateFlow(FeedMyAccountVOList(emptyList()))
    override val myAccounts: StateFlow<FeedMyAccountVOList> = _myAccounts.asStateFlow()

    init {
        scope.launch { _myAccounts.value = getMyAccounts() }
    }

    override suspend fun getMyAccounts(): FeedMyAccountVOList =
        service.getMyAccounts().toVO()
}
