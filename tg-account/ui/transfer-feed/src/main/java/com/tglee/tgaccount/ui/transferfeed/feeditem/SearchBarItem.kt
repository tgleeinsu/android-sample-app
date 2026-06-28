package com.tglee.tgaccount.ui.transferfeed.feeditem

import androidx.compose.runtime.Composable
import com.tglee.tgaccount.core.designsystem.component.TgSearchField
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedSearchBarState

@Composable
fun SearchBarItem(state: FeedSearchBarState) {
    TgSearchField(
        value = state.value,
        onValueChange = state.onValueChange,
        onClear = state.onClear,
    )
}
