package com.tglee.tgaccount.ui.transferfeed.feeditem

import androidx.compose.runtime.Composable
import com.tglee.tgaccount.core.designsystem.component.TgTextButtonRow
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedMyAccountMoreButtonState

@Composable
fun MyAccountMoreButton(state: FeedMyAccountMoreButtonState) {
    TgTextButtonRow(
        text = if (state.expanded) "접기" else "+${state.hiddenCount}개 더보기",
        onClick = state.onClick,
    )
}
