# 송금 샘플 앱 — 2차 기능 확장 플랜 (입금처 선택/송금 화면 추가 스펙)

## Context

1차로 2화면 송금 앱(입금처 선택 피드 + 송금)을 참조 아키텍처(11모듈, 멀티바인딩 피드, Navigation3, Hilt)로 구축·검증 완료했다(에뮬레이터 end-to-end 통과, `PLANNING.md` 참조). 이번 2차 작업은 **추가 스펙**을 기존 구조 위에 얹는 확장이다: 내 계좌 확장/축소 규칙 정교화, 실시간 검색(하이라이트 포함), [방금 송금] 뱃지·최상단 이동, 타입별 송금화면 표시, 금액 200만원 상한. 새 모듈은 만들지 않고 기존 슬라이스를 수정한다.

### 확정 결정 (사용자)
1. **[방금 송금] 뱃지·최상단 이동**: 내 계좌 포함 **모든 송금 완료 시** 해당 입금처를 [최근 보낸 계좌] 최상단에 등록/이동 + 뱃지. 단 하나(가장 최근)만 뱃지 유지. **앱 종료까지 in-memory 유지**(`@Singleton`).
2. **금액 200만원 초과 입력**: 입력 거부(이전 값 유지) + "200만원까지만 송금 가능합니다 [확인]" 다이얼로그.
3. 검색바는 피드의 첫 아이템(①)으로 유지하되 실제 입력 TextField 로 전환(멀티바인딩 충실, 안정 key `"search_bar"` + 상태 호이스팅).

---

## 변경 사항 (델타)

### A. core:navigation — `TransferSendKey` 보강 + 타입
`NavKeys.kt`: 송금화면이 타입별 표시(2-A) 및 [방금 송금] 식별을 위해 필드 확장.
```kotlin
enum class RecipientType { ACCOUNT, PHONE }   // @Serializable enum

@Serializable
data class TransferSendKey(
    val recipientId: String,
    val type: RecipientType,
    val name: String,
    val bankName: String = "",      // ACCOUNT 전용
    val accountNumber: String = "", // ACCOUNT 전용
    val phoneNumber: String = "",   // PHONE 전용
) : NavKey
```

### B. core:common — 방금 송금 공유 스토어 (신규)
`recent/JustSentStore.kt`: 송금(ui:transfer-send)이 쓰고 피드(ui:transfer-feed)가 읽는 앱 스코프 상태.
```kotlin
data class SentRecipient(val id, type: RecipientType, name, bankName, accountNumber, phoneNumber)

@Singleton
class JustSentStore @Inject constructor() {
    private val _justSent = MutableStateFlow<SentRecipient?>(null)
    val justSent: StateFlow<SentRecipient?> = _justSent.asStateFlow()
    fun markSent(recipient: SentRecipient) { _justSent.value = recipient }
}
```
(참조 아키텍처의 `StoryEventProvider`/Store 패턴 대응. `RecipientType` enum 은 core:common 에 두고 core:navigation 이 참조 — common 이 더 하위라 순환 없음.)

### C. core:designsystem — 검색 입력/하이라이트/뱃지
`component/TgComponents.kt`:
- `TgSearchField(value, onValueChange, onClear)` — 실제 입력 필드(기존 클릭형 `TgSearchBar` 대체). 우측 클리어(X) 아이콘.
- `TgListRow` 에 `highlight: String? = null`, `badge: String? = null` 파라미터 추가 → 제목/부제 매칭부 **bold**, 우측 [방금 송금] 칩.
- `highlightContains(text, query): AnnotatedString` — 대소문자 무시 `contains` 로 매칭 구간 `SpanStyle(fontWeight=Bold)`. 자모 분해 없음(단순 contains). query 공백이면 평문.

### D. domain:transfer-feed — 필드명/UiState 보강
- `vo/TransferFeedVo.kt`: `MyAccountVO.visibleWhenCollapsed` → **`showInCollapsed`** (스펙 필드명).
- `uistate/TransferFeedUiStates.kt`:
  - `SearchBarUiState(query: String)` — 현재 검색어 반영.
  - `MyAccountMoreButtonUiState(expanded, hiddenCount)` — 라벨 `"+{hiddenCount}개 더보기"` / `"접기"`.
  - `RecentAccountItemUiState`/`RecentPhoneItemUiState`: `justSent: Boolean` 추가.
  - (선택) `내 계좌` 섹션 헤더도 `FeedSectionHeaderUiState` 재사용.

### E. data:transfer-feed — 엔티티/목 JSON 필드명
- `entity/TransferFeedEntities.kt`: `MyAccountEntity.visibleWhenCollapsed` → `showInCollapsed`.
- `assets/my_accounts.json`: 키 `visibleWhenCollapsed` → `showInCollapsed`.
- `mapper/TransferFeedMappers.kt`: 매핑 갱신.

### F. ui:transfer-feed — 검색·확장축소·방금송금 (핵심)
- `state/TransferFeedStates.kt` `TransferFeedStateParam` 에 `query`, `onQueryChange`, `onClearQuery` 추가. 각 item State 에 `query`(하이라이트), recent State 에 `justSent` 반영.
- `item/TransferFeedItems.kt`: SearchBarItem → `TgSearchField`; 계좌/최근 item → `TgListRow(highlight=query, badge=if(justSent)"방금 송금")`. MoreButton 라벨 `"+N개 더보기"/"접기"`.
- `TransferFeedViewModel.kt` `rebuild()` 재작성:
  - 공통: 두 API 결과 캐시 + `JustSentStore.justSent` 병합. **최근 목록 = justSent(뱃지) ++ baseRecents.filter{ id != justSent.id }** (중복 제거, 뱃지 1개). 내 계좌로 송금 시 SentRecipient(ACCOUNT)도 최근 최상단에 삽입.
  - `query` 공백: ① 검색바 ② 내 계좌(`showInCollapsed==true` 또는 `expanded`) ③ `+N개 더보기`/`접기` (N=`showInCollapsed==false` 개수, 숨은 계좌 있을 때만) ④ 최근 헤더+목록.
  - `query` 비공백: ① 검색바 ② **매칭된 모든 내 계좌(숨김 포함, 접기/더보기 버튼 없음)** ③ 매칭된 최근 목록. 매칭 = 대소문자 무시 contains — 계좌: 이름/은행/계좌번호, 연락처: 이름/전화번호.
  - `init`: `JustSentStore.justSent` 를 collect → 새 값 도착(=송금 완료 복귀) 시 **검색어 초기화(query="")** + rebuild. (1-E 검색어 초기화 + 방금송금 반영 동시 처리.)
  - 회전 시 ViewModel 생존으로 query·expanded·캐시 유지(추가 작업 없음).
- 선택 콜백: `TransferSendKey(recipientId, type, name, bank, account, phone)` 구성해 push. (내 계좌→ACCOUNT, 최근 account→ACCOUNT, 최근 phone→PHONE.)

### G. ui:transfer-send — 타입별 표시·금액 상한
- `TransferSendViewModel.kt`:
  - `JustSentStore` 주입. `onClickSend(recipient: SentRecipient)` 완료 시 `store.markSent(recipient)` 후 `NavigateBackToFeed`.
  - 금액: `onAmountChange` 에서 정수 파싱 후 `> 2_000_000` 이면 무시 + `showMaxDialog=true`. `canSend = amount in 1..2_000_000 && !isSending`.
  - `showMaxDialog` 상태 + `onDismissMaxDialog`.
- `TransferSendRoute.kt`:
  - 헤더 2-A: `type==ACCOUNT` → 이름 / `은행 계좌번호`; `type==PHONE` → 이름 / `전화번호`.
  - `AlertDialog`(showMaxDialog): "200만원까지만 송금 가능합니다" + [확인].
  - 금액 입력 후 `onClickSend(SentRecipient(key...))` 호출. (프로그래스/입력차단/BackHandler 기존 유지.)
- `navigation/TransferSendEntryProvider.kt`: `entry<TransferSendKey> { key -> TransferSendRoute(key, onSendComplete={ backStack.removeLastOrNull() }) }` 유지(키 필드만 확장).

---

## 영향/주의
- `RecipientType` enum 위치는 **core:common** (common 이 더 하위, core:navigation → core:common 단방향, 순환 없음).
- 검색바 TextField 포커스: 안정 key + 호이스팅 상태로 유지. 키 입력마다 리스트 rebuild 되나 동일 key 로 포커스 보존. (이상 시 검색바를 고정 헤더로 분리하는 fallback.)
- 회전·API 미재호출·송금 흐름 등 1차 검증 항목은 회귀 확인만.

## 구현 순서
1. core 공통(enum/JustSentStore/TgSearchField·TgListRow 하이라이트·뱃지).
2. domain/data 필드명(`showInCollapsed`) + UiState 보강.
3. ui:transfer-feed(검색/확장축소/방금송금 rebuild).
4. ui:transfer-send(타입표시/금액상한/store 기록).
5. app 빌드 + 에뮬레이터 검증.

## 검증 (에뮬레이터 end-to-end)
- 확장/축소: 기본 축소(showInCollapsed=true 3개) → `+2개 더보기` → 5개+`접기` → 접기 복귀.
- 검색: "토" 입력 → 토스 계좌 노출(숨은 계좌 포함), 더보기/접기 숨김, 매칭부 bold. "토ㅅ" → 미노출. 빈 검색 → 전체 복원.
- 검색 후 입금처 선택 → 송금 완료 복귀 → 검색어 비워짐 + 해당 입금처 최근 최상단 [방금 송금] 뱃지.
- 송금화면: account 타입 이름/은행/계좌, phone 타입 이름/전화번호. 금액 200만 초과 → 다이얼로그+거부. 0/빈값 → 송금 버튼 disable. 10초 송금 회귀.
- 회전: 검색어/금액/확장상태 유지, API 미재호출.
