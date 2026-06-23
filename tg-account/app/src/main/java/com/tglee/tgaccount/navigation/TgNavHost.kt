package com.tglee.tgaccount.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.hilt.navigation.compose.hiltViewModel
import com.tglee.tgaccount.core.navigation.TransferFeedKey

/**
 * Navigation3 호스트. 시작 화면은 입금처 선택(TransferFeedKey).
 * - rememberNavBackStack: 회전/프로세스 종료 시 백스택 복원(@Serializable NavKey)
 * - ViewModelStore 디코레이터: NavEntry 스코프 ViewModel 이 회전에도 생존 → API 미재호출/입력값 유지
 * - 각 feature 의 NavEntryProvider 를 모아 한 곳에서 등록 (모듈별 분산 등록 컨셉)
 */
@Composable
fun TgNavHost(
    navViewModel: AppNavViewModel = hiltViewModel(),
) {
    val backStack = rememberNavBackStack(TransferFeedKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider<NavKey> {
            navViewModel.entryProviders.forEach { provider ->
                provider.register(this, backStack)
            }
        },
    )
}
