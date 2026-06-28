package com.tglee.tgaccount.ui.transferfeed.feeditem

import androidx.compose.runtime.Composable

@Composable
fun SearchBarItem(state: SearchBarState) {
    TgSearchField(
        value = state.value,
        onValueChange = state.onValueChange,
        onClear = state.onClear,
    )
}
