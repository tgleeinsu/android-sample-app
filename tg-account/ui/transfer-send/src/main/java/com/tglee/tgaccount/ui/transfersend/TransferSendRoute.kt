package com.tglee.tgaccount.ui.transfersend

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tglee.tgaccount.core.common.recent.RecipientType
import com.tglee.tgaccount.core.common.recent.SentRecipient
import com.tglee.tgaccount.core.designsystem.component.TgPrimaryButton
import com.tglee.tgaccount.core.navigation.TransferSendKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferSendRoute(
    key: TransferSendKey,
    onSendComplete: () -> Unit,
    viewModel: TransferSendViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 송금 완료 시 [방금 송금] 스토어에 기록할 입금처. NavKey 로부터 구성.
    val recipient = remember(key) {
        SentRecipient(
            id = key.recipientId,
            type = key.type,
            name = key.name,
            bankName = key.bankName,
            accountNumber = key.accountNumber,
            phoneNumber = key.phoneNumber,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TransferSendEffect.NavigateBackToFeed -> onSendComplete()
            }
        }
    }

    // 송금 진행 중에는 뒤로가기 차단
    BackHandler(enabled = uiState.isSending) { /* consume */ }

    Scaffold(
        topBar = { TopAppBar(title = { Text("송금") }) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 상단(2-A): 타입별 표시. ACCOUNT → 이름/은행 계좌번호, PHONE → 이름/전화번호.
                Text(
                    text = "${key.name}님에게",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = when (key.type) {
                        RecipientType.ACCOUNT -> "${key.bankName} ${key.accountNumber}"
                        RecipientType.PHONE -> key.phoneNumber
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text("보낼 금액") },
                    suffix = { Text("원") },
                    singleLine = true,
                    enabled = !uiState.isSending,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(Modifier.weight(1f))

                TgPrimaryButton(
                    text = "송금하기",
                    onClick = { viewModel.onClickSend(recipient) },
                    enabled = uiState.canSend,
                )
            }

            // 200만원 초과 입력 안내 다이얼로그
            if (uiState.showMaxDialog) {
                AlertDialog(
                    onDismissRequest = viewModel::onDismissMaxDialog,
                    confirmButton = {
                        TextButton(onClick = viewModel::onDismissMaxDialog) { Text("확인") }
                    },
                    text = { Text("200만원까지만 송금 가능합니다") },
                )
            }

            // 진행 중 오버레이: 스크림 + 프로그래스 + 전체 입력 차단(터치 소비)
            if (uiState.isSending) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .pointerInput(Unit) { /* 모든 터치 소비 */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator()
                        Text("송금 중입니다...", color = Color.White)
                    }
                }
            }
        }
    }
}
