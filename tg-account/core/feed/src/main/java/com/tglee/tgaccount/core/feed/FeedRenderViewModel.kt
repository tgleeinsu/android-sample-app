package com.tglee.tgaccount.core.feed

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Hilt 멀티바인딩 맵을 주입받아 UiState -> State -> Composable 매핑을 수행한다.
 * UiState→State→Composable 매핑을 한곳에서 수행하는 단순화 버전.
 */
@HiltViewModel
class FeedRenderViewModel @Inject constructor(
    private val stateProviders: Map<Class<*>, @JvmSuppressWildcards ViewTypeStateProvider<*>>,
    private val renderers: Map<Class<*>, @JvmSuppressWildcards FeedItemRenderer>,
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    @Composable
    fun rememberState(uiState: FeedItemUiState, param: FeedItemStateParam): FeedItemState? {
        val provider = stateProviders[uiState::class.java] as? ViewTypeStateProvider<FeedItemUiState>
        return provider?.rememberState(uiState, param)
    }

    fun rendererFor(state: FeedItemState): FeedItemRenderer? = renderers[state::class.java]
}
