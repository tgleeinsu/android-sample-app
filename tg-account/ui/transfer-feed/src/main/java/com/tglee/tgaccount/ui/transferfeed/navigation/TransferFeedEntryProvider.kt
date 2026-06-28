package com.tglee.tgaccount.ui.transferfeed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.tglee.tgaccount.core.navigation.NavEntryProvider
import com.tglee.tgaccount.core.navigation.TransferFeedKey
import com.tglee.tgaccount.ui.transferfeed.TransferScreen
import com.tglee.tgaccount.ui.transferfeed.rememberTransferScreenState
import javax.inject.Inject

/** 입금처 선택 화면 entry 등록. 아이템 선택 시 송금 화면(TransferSendKey)을 백스택에 push. */
internal class TransferFeedEntryProvider @Inject constructor() : NavEntryProvider {
    override fun register(scope: EntryProviderScope<NavKey>, backStack: NavBackStack<NavKey>) {
        scope.entry<TransferFeedKey> {
            TransferScreen(
                screenState = rememberTransferScreenState(
                    onSelectRecipient = { sendKey -> backStack.add(sendKey) },
                ),
            )
        }
    }
}

