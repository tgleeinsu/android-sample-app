package com.tglee.tgaccount.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
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
 * 대소문자 무시 contains 로 [query] 와 일치하는 [text] 구간을 Bold 로 강조한 AnnotatedString 을 만든다.
 * 자모 분해는 하지 않는 단순 contains. [query] 가 공백이거나 미일치면 평문을 반환한다.
 */
fun highlightContains(text: String, query: String): AnnotatedString {
    val q = query.trim()
    if (q.isEmpty()) return AnnotatedString(text)
    val start = text.indexOf(q, ignoreCase = true)
    if (start < 0) return AnnotatedString(text)
    val end = start + q.length
    return buildAnnotatedString {
        append(text.substring(0, start))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(text.substring(start, end))
        }
        append(text.substring(end))
    }
}

/** 실제 입력형 검색 필드(피드 첫 아이템). 우측 클리어(X) 아이콘 포함. */
@Composable
fun TgSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "계좌번호, 이름으로 검색",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(text = hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                inner()
            },
        )
        if (value.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "검색어 지우기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onClear),
            )
        }
    }
}

/**
 * 제목/부제목/아이콘으로 구성된 목록 행. 계좌·연락처 공통 사용.
 * [highlight] 가 있으면 제목/부제 매칭부를 Bold 로, [badge] 가 있으면 우측에 칩을 표시한다.
 */
@Composable
fun TgListRow(
    title: String,
    subtitle: String,
    iconUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlight: String? = null,
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
            Text(
                text = highlightContains(title, highlight.orEmpty()),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = highlightContains(subtitle, highlight.orEmpty()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = badge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
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
