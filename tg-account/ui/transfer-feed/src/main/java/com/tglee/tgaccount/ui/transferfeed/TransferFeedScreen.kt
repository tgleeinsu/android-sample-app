package com.tglee.tgaccount.ui.transferfeed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tglee.tgaccount.core.feed.FeedLazyColumn
import com.tglee.tgaccount.domain.transferfeed.screenuistate.TransferScreenUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransferScreen(
    screenState: TransferScreenState,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("입금처 선택") })
        }
    ) { padding ->
        when (val uiState = screenState.screenUiState) {
            is TransferScreenUiState.Loaded -> {
                FeedLazyColumn(
                    items = uiState.items,
                    param = param,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = padding,
                )
            }

            is TransferScreenUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}