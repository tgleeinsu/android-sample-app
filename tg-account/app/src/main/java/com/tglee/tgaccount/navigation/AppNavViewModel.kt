package com.tglee.tgaccount.navigation

import androidx.lifecycle.ViewModel
import com.tglee.tgaccount.core.navigation.NavEntryProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** 각 feature 모듈이 @IntoSet 으로 기여한 NavEntryProvider 집합을 NavHost 로 전달한다. */
@HiltViewModel
class AppNavViewModel @Inject constructor(
    val entryProviders: Set<@JvmSuppressWildcards NavEntryProvider>,
) : ViewModel()
