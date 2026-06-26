package com.tglee.tgaccount.ui.transferfeed.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tglee.tgaccount.core.designsystem.component.TgListRow
import com.tglee.tgaccount.core.designsystem.component.TgSearchField
import com.tglee.tgaccount.core.designsystem.component.TgTextButtonRow
import com.tglee.tgaccount.ui.transferfeed.state.MyAccountItemState
import com.tglee.tgaccount.ui.transferfeed.state.MyAccountMoreButtonState
import com.tglee.tgaccount.ui.transferfeed.state.RecentAccountItemState
import com.tglee.tgaccount.ui.transferfeed.state.RecentPhoneItemState
import com.tglee.tgaccount.ui.transferfeed.state.SearchBarState
import com.tglee.tgaccount.ui.transferfeed.state.SectionHeaderState

/**
 * 각 FeedItemState 에 대응하는 @Composable. (운영에선 @UniversalItem 어노테이션이 붙는 계층5)
 */

private const val JUST_SENT_BADGE = "방금 송금"

@Composable
fun SearchBarItem(state: SearchBarState) {
    TgSearchField(
        value = state.value,
        onValueChange = state.onValueChange,
        onClear = state.onClear,
    )
}

@Composable
fun MyAccountItem(state: MyAccountItemState) {
    val ui = state.uiState
    TgListRow(
        title = ui.accountName,
        subtitle = "${ui.bankName} ${ui.accountNumber}",
        iconUrl = ui.iconUrl,
        onClick = state.onClick,
        highlight = state.query,
    )
}

@Composable
fun MyAccountMoreButton(state: MyAccountMoreButtonState) {
    TgTextButtonRow(
        text = if (state.expanded) "접기" else "+${state.hiddenCount}개 더보기",
        onClick = state.onClick,
    )
}

@Composable
fun RecentAccountItem(state: RecentAccountItemState) {
    val ui = state.uiState
    TgListRow(
        title = ui.name,
        subtitle = "${ui.bankName} ${ui.accountNumber}",
        iconUrl = ui.iconUrl,
        onClick = state.onClick,
        highlight = state.query,
        badge = if (ui.justSent) JUST_SENT_BADGE else null,
    )
}

@Composable
fun RecentPhoneItem(state: RecentPhoneItemState) {
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

@Composable
fun SectionHeader(state: SectionHeaderState) {
    Text(
        text = state.title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}
