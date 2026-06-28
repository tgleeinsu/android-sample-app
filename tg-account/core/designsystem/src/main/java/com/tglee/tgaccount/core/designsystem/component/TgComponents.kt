package com.tglee.tgaccount.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/** 원형 아바타. mock 아이콘 url 을 Coil 로 로드한다. */
@Composable
fun TgAvatar(
    url: String?,
    modifier: Modifier = Modifier,
    size: Int = 40,
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

/**
 * 제목/부제목/아이콘으로 구성된 목록 행. 계좌·연락처 공통 사용.
 * [highlight] 가 비어있지 않으면 제목·부제목에서 일치 구간을 강조한다.
 * [badge] 가 있으면 제목 옆에 작은 라벨("방금 송금" 등)을 붙인다.
 */
@Composable
fun TgListRow(
    title: String,
    subtitle: String,
    iconUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlight: String = "",
    badge: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TgAvatar(url = iconUrl)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = highlighted(title, highlight, MaterialTheme.colorScheme.primary),
                    fontWeight = FontWeight.SemiBold,
                )
                if (badge != null) {
                    Spacer(Modifier.width(6.dp))
                    TgBadge(text = badge)
                }
            }
            Text(
                text = highlighted(subtitle, highlight, MaterialTheme.colorScheme.primary),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/** 작은 강조 라벨(뱃지). */
@Composable
private fun TgBadge(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/** 검색 입력 필드. 값이 있으면 우측에 지우기(✕) 버튼을 노출한다. */
@Composable
fun TgSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "이름·은행·계좌번호 검색",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        singleLine = true,
        trailingIcon = {
            if (value.isNotEmpty()) {
                Text(
                    text = "✕",
                    modifier = Modifier
                        .clickable(onClick = onClear)
                        .padding(horizontal = 12.dp),
                )
            }
        },
    )
}

/** 하단 고정형 기본 버튼. */
@Composable
fun TgPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

/** 가운데 정렬 텍스트 버튼(더보기 등). */
@Composable
fun TgTextButtonRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
    }
}

/** [query] 와 일치하는 구간(대소문자 무시)을 [color]·Bold 로 강조한 AnnotatedString. */
private fun highlighted(text: String, query: String, color: Color): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text)
    val lowerText = text.lowercase()
    val lowerQuery = query.lowercase()
    if (!lowerText.contains(lowerQuery)) return AnnotatedString(text)
    return buildAnnotatedString {
        var start = 0
        while (true) {
            val idx = lowerText.indexOf(lowerQuery, start)
            if (idx < 0) {
                append(text.substring(start))
                break
            }
            append(text.substring(start, idx))
            withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                append(text.substring(idx, idx + query.length))
            }
            start = idx + query.length
        }
    }
}
