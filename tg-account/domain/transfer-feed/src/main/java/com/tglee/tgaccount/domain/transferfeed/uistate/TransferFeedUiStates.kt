package com.tglee.tgaccount.domain.transferfeed.uistate

import com.tglee.tgaccount.core.feed.FeedItemUiState

/**
 * 입금처 선택 피드를 구성하는 뷰타입들. 모두 [FeedItemUiState] 를 구현해
 * 하나의 List<FeedItemUiState> 로 조합되어 FeedLazyColumn 에 렌더링된다.
 */

/** ① 검색바. 현재 검색어를 반영한다. */
data class SearchBarUiState(
    val query: String = "",
    override val id: String = "search_bar",
) : FeedItemUiState

/** ② 내 계좌 아이템. */
data class MyAccountItemUiState(
    override val id: String,
    val accountName: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
) : FeedItemUiState

/** ③ 내 계좌 더보기/접기 버튼. 라벨은 "+{hiddenCount}개 더보기" / "접기". */
data class MyAccountMoreButtonUiState(
    val expanded: Boolean,
    val hiddenCount: Int,
    override val id: String = "my_account_more",
) : FeedItemUiState

/** ④ 최근 보낸 계좌(account 타입). [justSent] 면 [방금 송금] 뱃지 대상. */
data class RecentAccountItemUiState(
    override val id: String,
    val name: String,
    val accountNumber: String,
    val bankName: String,
    val iconUrl: String?,
    val justSent: Boolean = false,
) : FeedItemUiState

/** ④ 최근 보낸 상대(phone 타입). [justSent] 면 [방금 송금] 뱃지 대상. */
data class RecentPhoneItemUiState(
    override val id: String,
    val name: String,
    val phoneNumber: String,
    val iconUrl: String?,
    val justSent: Boolean = false,
) : FeedItemUiState

/** 섹션 헤더(최근 보낸 계좌 구분용, 선택적). */
data class FeedSectionHeaderUiState(
    override val id: String,
    val title: String,
) : FeedItemUiState
