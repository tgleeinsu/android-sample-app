package com.tglee.tgaccount.ui.transferfeed.feeditem

import androidx.compose.runtime.Composable
import com.tglee.tgaccount.core.designsystem.component.TgListRow
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedRecentPhoneItemState

private const val JUST_SENT_BADGE = "방금 송금"

@Composable
fun RecentPhoneItem(state: FeedRecentPhoneItemState) {
    val ui = state.uiState
    TgListRow(
        title = ui.name,
        subtitle = ui.phoneNumber,
        iconUrl = ui.iconUrl,
        onClick = state.onClick,
        highlight = state.query,
        badge = if (ui.justSent) JUST_SENT_BADGE else null,
    )
}
