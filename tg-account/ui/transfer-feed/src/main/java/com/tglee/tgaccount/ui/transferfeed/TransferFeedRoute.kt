package com.tglee.tgaccount.ui.transferfeed

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tglee.tgaccount.core.feed.FeedLazyColumn
import com.tglee.tgaccount.core.navigation.TransferSendKey
import com.tglee.tgaccount.ui.transferfeed.state.TransferFeedStateParam

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferFeedRoute(
    onSelectRecipient: (TransferSendKey) -> Unit,
    viewModel: TransferFeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 에러 토스트 (SideEffect)
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TransferFeedEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val param = remember(onSelectRecipient) {
        TransferFeedStateParam(
            onClickSearch = {
                Toast.makeText(context, "검색은 샘플에서 생략되었습니다.", Toast.LENGTH_SHORT).show()
            },
            onToggleMyAccountMore = viewModel::onToggleMyAccountMore,
            onSelectMyAccount = { acc ->
                onSelectRecipient(
                    TransferSendKey(
                        displayName = acc.accountName,
                        bankName = acc.bankName,
                        accountNumber = acc.accountNumber,
                    ),
                )
            },
            onSelectRecentAccount = { rcp ->
                onSelectRecipient(
                    TransferSendKey(
                        displayName = rcp.name,
                        bankName = rcp.bankName,
                        accountNumber = rcp.accountNumber,
                    ),
                )
            },
            onSelectRecentPhone = { rcp ->
                onSelectRecipient(
                    TransferSendKey(
                        displayName = rcp.name,
                        bankName = "",
                        accountNumber = rcp.phoneNumber,
                    ),
                )
            },
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("입금처 선택") }) },
    ) { padding ->
        when (val state = uiState) {
            is TransferFeedUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is TransferFeedUiState.Error -> {
                // 실패 시 화면 비우기
                Box(Modifier.fillMaxSize().padding(padding))
            }

            is TransferFeedUiState.Loaded -> {
                FeedLazyColumn(
                    items = state.items,
                    param = param,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = padding,
                )
            }
        }
    }
}
