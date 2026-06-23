package com.tglee.tgaccount.ui.transfersend.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.tglee.tgaccount.core.navigation.NavEntryProvider
import com.tglee.tgaccount.core.navigation.TransferSendKey
import com.tglee.tgaccount.ui.transfersend.TransferSendRoute
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

/** 송금 화면 entry 등록. 송금 완료 시 백스택에서 pop 하여 입금처 선택 화면으로 복귀. */
class TransferSendEntryProvider @Inject constructor() : NavEntryProvider {
    override fun register(scope: EntryProviderScope<NavKey>, backStack: NavBackStack<NavKey>) {
        scope.entry<TransferSendKey> { key ->
            TransferSendRoute(
                key = key,
                onSendComplete = { backStack.removeLastOrNull() },
            )
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface TransferSendNavModule {
    @Binds
    @IntoSet
    fun bindEntryProvider(impl: TransferSendEntryProvider): NavEntryProvider
}
