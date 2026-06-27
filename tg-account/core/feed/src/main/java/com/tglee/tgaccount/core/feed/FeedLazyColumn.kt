package com.tglee.tgaccount.core.feed

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tglee.tgaccount.core.feed.marker.FeedItemStateParam
import com.tglee.tgaccount.core.feed.marker.FeedUiState

/**
 * 서로 다른 뷰타입의 [com.tglee.tgaccount.core.feed.marker.FeedUiState] 리스트를 하나의 리스트로 polymorphic 렌더링한다.
 * 각 아이템은 멀티바인딩 맵을 통해 UiState -> FeedItemState -> Composable 로 변환된다.
 */
@Composable
fun FeedLazyColumn(
    items: List<FeedUiState>,
    param: FeedItemStateParam,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: FeedRenderViewModel = hiltViewModel(),
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        items(
            items = items,
            key = { it.id },
            contentType = { it::class.java.name },
        ) { uiState ->
            val state = viewModel.rememberState(uiState, param)
            if (state != null) {
                val renderer = viewModel.rendererFor(state)
                renderer?.Render(state)
            }
        }
    }
}
