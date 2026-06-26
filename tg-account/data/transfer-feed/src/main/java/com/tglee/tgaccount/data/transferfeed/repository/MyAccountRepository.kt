package com.tglee.tgaccount.data.transferfeed.repository

import com.tglee.tgaccount.data.transferfeed.mapper.toVO
import com.tglee.tgaccount.data.transferfeed.service.MyAccountService
import com.tglee.tgaccount.data.transferfeed.vo.MyAccountVO
import javax.inject.Inject

interface MyAccountRepository {
    suspend fun getMyAccounts(): List<MyAccountVO>
}

internal class MyAccountRepositoryImpl @Inject constructor(
    private val service: MyAccountService,
) : MyAccountRepository {
    override suspend fun getMyAccounts(): List<MyAccountVO> =
        service.getMyAccounts().map { it.toVO() }
}


