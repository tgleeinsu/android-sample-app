package com.tglee.tgaccount.core.feed.marker

import androidx.compose.runtime.Composable

/**
 * 서버 드리븐 멀티바인딩 피드 프레임워크의 계약(contract).
 * @UniversalItem + KSP 자동등록 대신, KSP 없이 Hilt 멀티바인딩으로 구성한 형태.
 *
 * 한 아이템을 그리려면 두 등록이 짝으로 필요하다.
 *  - 계층4: [ViewTypeStateProvider] (UiState -> FeedItemState),  @IntoMap @ClassKey(XxxUiState::class)
 *  - 계층5: [FeedItemRenderer]      (FeedItemState -> Composable), @IntoMap @ClassKey(XxxState::class)
 *    (운영 프로젝트에선 계층5를 @UniversalItem 어노테이션 + KSP 가 자동 생성한다.)
 */

/** 피드에 들어가는 데이터(뷰타입) 단위. ViewModel 이 여러 API 응답을 이 타입의 리스트로 조합한다. */
interface FeedUiState {
    /** LazyColumn 의 안정적 key. */
    val id: String
}

/** 화면 컨텍스트(콜백/네비게이션)를 담아 UiState->State 변환 시 주입하는 파라미터. */
interface FeedItemStateParam

/** UiState 에서 파생된, 콜백까지 바인딩된 렌더링용 상태. */
interface FeedItemState {
    fun getKey(): String
}

/** UiState -> FeedItemState 변환기. feature 모듈이 구현해 @IntoMap @ClassKey(UiState) 로 등록. */
interface ViewTypeStateProvider<in T : FeedUiState> {
    @Composable
    fun rememberState(uiState: T, param: FeedItemStateParam): FeedItemState?
}

/** FeedItemState -> Composable 렌더러. feature 모듈이 구현해 @IntoMap @ClassKey(State) 로 등록. */
interface FeedItemRenderer {
    @Composable
    fun Render(state: FeedItemState)
}
