package com.tglee.tgaccount.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 각 feature 모듈이 자신의 화면 entry 를 등록한다. Hilt @IntoSet 으로 모아
 * app 의 NavDisplay 가 한 곳에서 수집한다. (모듈별 분산 등록 컨셉)
 */
interface NavEntryProvider {
    fun register(scope: EntryProviderScope<NavKey>, backStack: NavBackStack<NavKey>)
}
