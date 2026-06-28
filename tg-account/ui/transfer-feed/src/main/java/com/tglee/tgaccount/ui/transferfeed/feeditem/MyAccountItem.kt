package com.tglee.tgaccount.ui.transferfeed.feeditem

import androidx.compose.runtime.Composable
import com.tglee.tgaccount.core.designsystem.component.TgListRow
import com.tglee.tgaccount.ui.transferfeed.feeditem.state.FeedMyAccountItemState

@Composable
fun MyAccountItem(state: FeedMyAccountItemState) {
    val ui = state.uiState
    TgListRow(
        title = ui.accountName,
        subtitle = "${ui.bankName} ${ui.accountNumber}",
        iconUrl = ui.iconUrl,
        onClick = state.onClick,
    )
}
