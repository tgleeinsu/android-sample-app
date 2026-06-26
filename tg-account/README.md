# TG Account — 송금 샘플 앱 설명서

**Google 권장 아키텍처(Modern App Architecture)** 기반으로 구성한 **2화면 송금 샘플 앱**.
입금처 선택 피드(검색/확장축소/방금송금)와 송금 화면(타입별 표시/금액 상한/10초 fake 송금)을 11개 멀티모듈 위에 올렸다.

- AGP 9.2.x · Kotlin 2.2.x · Compose BOM 2026.02 · minSdk 24 / targetSdk 36 / compileSdk 37
- 빌드 인프라: `buildSrc` precompiled convention plugin 3종 (`tg.android.library`, `tg.android.compose`, `tg.android.hilt`)
- 멀티바인딩 피드 + Navigation3 + Hilt
- 상세 기획: [`PLANNING.md`](./PLANNING.md)(1차) · [`PLANNING-2.md`](./PLANNING-2.md)(2차 확장)

> **아키텍처 한 줄 요약** — Google Modern App Architecture: 레이어는 **UI → Domain(optional) → Data** 로 의존이
> 한 방향으로만 내려간다. repository **인터페이스와 구현이 모두 Data 레이어에 위치**하고(데이터 레이어의 공개 API),
> Domain 의 use case 가 그 인터페이스에 의존한다. (의존성 역전으로 repo 인터페이스를 domain 에 두는 Clean Architecture 와 구분된다.)

---

## 1. 전체 워크플로우 (런타임 흐름)

사용자 입력 → UI → ViewModel → UseCase → Repository → DataSource(Fake) → assets JSON 으로 내려갔다가,
데이터 모델로 매핑되어 UiState 리스트로 조합되고, 멀티바인딩 피드를 통해 다시 화면으로 올라온다.

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  MainActivity (@AndroidEntryPoint)                                             │
│    └─ TgTheme → TgNavHost                                                      │
│         └─ NavDisplay( backStack = rememberNavBackStack(TransferFeedKey),      │
│              decorators = [ SaveableStateHolder, ViewModelStore ],  ← 회전 생존 │
│              entryProvider = AppNavViewModel.entryProviders.forEach{ register } │
│            )                                                                    │
└───────────────┬───────────────────────────────────────────┬──────────────────┘
        TransferFeedKey                                TransferSendKey
                │                                              │
   ┌────────────▼─────────────┐                  ┌────────────▼──────────────┐
   │  [화면 A] 입금처 선택       │   아이템 선택        │  [화면 B] 송금              │
   │  TransferFeedRoute  (UI)  │ ──TransferSendKey─▶│  TransferSendRoute  (UI)  │
   │   collect uiState/query   │  (id,type,name,..) │   collect uiState         │
   └────────────┬─────────────┘                    └────────────┬─────────────┘
                │ hiltViewModel()                                │ hiltViewModel()
   ┌────────────▼─────────────┐                    ┌────────────▼─────────────┐
   │  TransferFeedViewModel    │                    │  TransferSendViewModel    │
   │  init{ load(); collect    │                    │  onAmountChange(상한 200만)│
   │        justSentStore }    │                    │  onClickSend(recipient)   │
   │  rebuild() → UiState 리스트│                    │     │                     │
   └───┬───────────────┬──────┘                    │     ▼                     │
       │ getMyAccounts │ getRecentRecipients        │  SendMoneyUseCase (domain)│
       ▼ (domain)      ▼ (domain)                   │     │                     │
   GetMyAccountsUseCase  GetRecentRecipientsUseCase │     ▼                     │
       │               │                            │  TransferSendRepository   │
       ▼ (data)        ▼ (data)                     │   (data, interface)       │
   MyAccountRepository  RecentRecipientRepository   │     │ Impl                │
       │ (interface+Impl, Mapper Entity→Model)      │     ▼                     │
       ▼               ▼                            │  FakeTransferSendDataSrc  │
   FakeMyAccountService  FakeRecentRecipientService │   delay(10_000) (fake)    │
       │ delay(400) + 실패토글  (DataSource)         │     │ 완료                │
       ▼               ▼                            │     ▼                     │
   AssetJsonLoader (assets/*.json)                  │  JustSentStore.markSent() │◀─┐
   my_accounts.json / recent_recipients.json        │     │                     │  │ @Singleton
                                                    │     ▼                     │  │ in-memory
                                                    │  Effect.NavigateBackToFeed│  │ 공유
                                                    └────────────┬─────────────┘  │
                                                                 │ backStack.pop  │
                                                   ┌─────────────▼─────────────┐  │
                                                   │ 피드 복귀: justSent collect │──┘
                                                   │ → query="" + rebuild        │
                                                   │ → [방금송금] 뱃지·최상단 이동 │
                                                   └─────────────────────────────┘
```

**핵심 흐름 포인트**
- **단방향 데이터 흐름**: UI 이벤트 → ViewModel 상태변경 → `StateFlow` 방출 → 재구성. SideEffect 는 `Channel`(`NavigateBackToFeed`, `ShowError`).
- **회전 안정성**: ViewModel 이 NavEntry 스코프(`rememberViewModelStoreNavEntryDecorator`)라 회전 시 파괴되지 않음 → `init` 의 API 로드가 재실행되지 않고 입력값(query/amount/expanded)도 유지.
- **방금 송금(cross-screen)**: 송금 완료 시 `JustSentStore`(@Singleton)에 기록 → 피드 ViewModel 이 `collect` 로 감지 → 검색어 초기화 + 최근 목록 최상단 이동 + 뱃지(가장 최근 1건).
- **금액 상한**: `onAmountChange` 에서 200만원 초과 입력은 거부(이전 값 유지) + 안내 다이얼로그.

---

## 2. 모듈 간 의존성 관계 (11개 모듈)

**Google Modern App Architecture**: 컴파일 의존이 `UI → Domain → Data` 로 **한 방향으로만 내려간다.**
화살표는 "의존한다(→)" 방향이며, **Data 가 최하위(다른 feature 모듈에 의존하지 않음)** 이다.

```
                              ┌─────────┐
                              │  :app   │  @HiltAndroidApp / NavDisplay 호스트
                              └────┬────┘
        ┌──────────────┬──────────┼───────────┬──────────────────┐
        ▼              ▼          ▼            ▼                  ▼
  :ui:transfer-feed        :ui:transfer-send   :core:feed   :core:designsystem
   (UI Layer)               (UI Layer)         (멀티바인딩 계약)  (Compose 컴포넌트)
        │                        │
        │ → domain, data, core   │ → domain, core
        ▼                        ▼
  :domain:transfer-feed     :domain:transfer-send         (Domain Layer: use case)
        │                        │
        │ api → data             │ → data
        ▼                        ▼
  :data:transfer-feed       :data:transfer-send           (Data Layer: 최하위)
        │  repository(if+impl)    │ repository(if+impl)
        │  + model + datasource   │ + datasource
        ▼                        (coroutines)
  :core:common ◀── :core:navigation (api, RecipientType/JustSentStore)
  (최하위 공용)
```

**레이어 분류**

| 레이어 | 모듈 | 역할 |
|--------|------|------|
| **UI** | `:ui:transfer-feed`, `:ui:transfer-send` (+`:app` 호스트) | Compose 화면 · ViewModel · **UI State** |
| **Domain** | `:domain:transfer-feed`, `:domain:transfer-send` | use case (data repository 인터페이스에 의존) |
| **Data** | `:data:transfer-feed`, `:data:transfer-send` | **repository 인터페이스+구현** · 데이터 소스 · 모델 |
| **Core** | `:core:common`, `:core:designsystem`, `:core:navigation`, `:core:feed` | 레이어 공통 기반 |

**의존성 표 (project 의존만 표기)**

| 모듈 | 의존 대상 |
|------|-----------|
| `:app` | `:core:navigation`, `:core:designsystem`, `:core:feed`, `:ui:transfer-feed`, `:ui:transfer-send` |
| `:ui:transfer-feed` | `:domain:transfer-feed`, `:data:transfer-feed`, `:core:common`, `:core:feed`, `:core:designsystem`, `:core:navigation` |
| `:ui:transfer-send` | `:domain:transfer-send`, `:core:common`, `:core:designsystem`, `:core:navigation` |
| `:domain:transfer-feed` | **api** `:data:transfer-feed` |
| `:domain:transfer-send` | `:data:transfer-send` |
| `:data:transfer-feed` | `:core:common` (※ domain 에 의존하지 않음) |
| `:data:transfer-send` | (없음 — coroutines 라이브러리만) |
| `:core:navigation` | **api** `:core:common` |
| `:core:feed` / `:core:designsystem` / `:core:common` | (없음 — 라이브러리만) |

**규칙**
- `ui → domain → data` 단방향. **Data 레이어가 최하위**라 domain/ui 를 전혀 모른다 → 의존성 역전이 없다(= Clean 과 구분되는 지점).
- repository **인터페이스+구현이 모두 data 에** 있고(데이터 레이어의 공개 API), domain 의 use case 가 그 인터페이스를 호출한다.
- data 가 노출하는 **모델**(`MyAccountVO`, `RecentRecipientVO`)을 use case 가 반환하므로, `domain → data` 는 `api` 로 전파해 UI 가 모델을 함께 본다.
- **UI State**(`SearchBarUiState` 등)는 UI 관심사라 `ui` 레이어에 둔다.
- `core:common` 이 최하위 공용. `core:navigation` 이 `RecipientType`/`JustSentStore` 를 노출하려고 `api(:core:common)` 전파(순환 없음).

---

## 3. 멀티바인딩 피드 워크플로우

서버 드리븐 다형 피드를 KSP 없이 **Hilt `@IntoMap @ClassKey`** 두 단계 맵으로 구성. 새 뷰타입 추가 = `@IntoMap` 2개 등록.

```
  ViewModel.rebuild()
        │  여러 데이터 모델(VO)을 하나의 List<FeedItemUiState> 로 조합
        ▼
  List<FeedItemUiState>  ── SearchBarUiState, MyAccountItemUiState, MoreButton,
        │                    RecentAccountItemUiState, RecentPhoneItemUiState, SectionHeader …
        ▼
  FeedLazyColumn( items, param )
        │  items(key = id, contentType = uiState::class)
        ▼
  FeedRenderViewModel  ◀── Hilt 주입 ──┐
        │                              │
        │  ① rememberState(uiState)    │   Map<Class<*>, ViewTypeStateProvider<*>>   ← 계층4
        │      provider = map[uiState::class]                                         (@IntoMap @ClassKey(XxxUiState))
        │      provider.rememberState(uiState, param) ──▶ FeedItemState              ViewTypeStateModule
        │                              │
        │  ② rendererFor(state)        │   Map<Class<*>, FeedItemRenderer>           ← 계층5
        │      renderer = map[state::class]                                           (@IntoMap @ClassKey(XxxState))
        │      renderer.Render(state) ──▶ @Composable                               FeedRendererModule
        ▼
  화면에 행 렌더 (TgSearchField / TgListRow(highlight,badge) / TgTextButtonRow / …)


  한 뷰타입 = 3요소 + 2등록의 짝:
  ┌────────────────────────────────────────────────────────────────────────────┐
  │  XxxUiState (ui/uistate)      XxxState (ui/state)        XxxItem (ui/item)    │
  │   : FeedItemUiState     ──▶    : FeedItemState     ──▶    @Composable         │
  │     val id                       getKey()                                     │
  │        │                            ▲                          ▲             │
  │        │  계층4 등록: @IntoMap @ClassKey(XxxUiState::class)      │             │
  │        └──ViewTypeStateProvider ────┘                          │             │
  │                                     계층5 등록: @IntoMap @ClassKey(XxxState)  │
  │                                     └──FeedItemRenderer ───────┘             │
  └────────────────────────────────────────────────────────────────────────────┘
        @Multibinds 로 빈 맵 기본 제공(FeedMultibindsModule) → 어떤 feature 도 미등록 가능
```

**키 포인트**
- **계약은 `core:feed`**: `FeedItemUiState`/`FeedItemState`/`ViewTypeStateProvider`/`FeedItemRenderer`/`FeedLazyColumn`/`FeedRenderViewModel`.
- **계층4 (`ViewTypeStateProvider`)**: `UiState → FeedItemState`. 화면 콜백/검색어(`FeedItemStateParam = TransferFeedStateParam`)를 주입해 클릭 람다·하이라이트 `query` 를 바인딩.
- **계층5 (`FeedItemRenderer`)**: `FeedItemState → @Composable`. (운영에선 `@UniversalItem` + KSP 가 자동 생성하는 부분)
- **두 맵의 키가 다름**: 계층4 는 `UiState::class`, 계층5 는 `State::class`. `FeedRenderViewModel` 이 두 맵을 잇는다.
- **안정 key/contentType**: `LazyColumn` 이 `id`/클래스명으로 재사용 → 검색바(`"search_bar"`)는 키 입력마다 리스트가 rebuild 돼도 포커스 유지.

---

## 4. 클래스 구조 관계성 트리

```
:app  (UI 호스트)
 ├─ TgAccountApp                         @HiltAndroidApp
 ├─ MainActivity                         @AndroidEntryPoint → TgTheme → TgNavHost
 └─ navigation/
     ├─ TgNavHost                        NavDisplay + 디코레이터2 + entryProvider 수집
     └─ AppNavViewModel                  Set<@JvmSuppressWildcards NavEntryProvider> 주입

:core
 ├─ common/                              ◀ 최하위 공용
 │   ├─ recent/RecipientType            @Serializable enum { ACCOUNT, PHONE }
 │   ├─ recent/JustSentStore            @Singleton  ─ StateFlow<SentRecipient?> / markSent()
 │   ├─ recent/SentRecipient            data class (id,type,name,bank,account,phone)
 │   ├─ asset/AssetJsonLoader           @Singleton  ─ assets JSON → @Serializable
 │   ├─ result/AppResult · di/CommonModule (Json @Provides)
 ├─ navigation/
 │   ├─ NavKey:  TransferFeedKey (data object)
 │   │           TransferSendKey (data class: recipientId,type,name,bank,account,phone)
 │   └─ NavEntryProvider                interface register(scope, backStack)
 ├─ feed/                               ◀ 멀티바인딩 프레임워크(계약)
 │   ├─ FeedItemUiState · FeedItemStateParam · FeedItemState (if)
 │   ├─ ViewTypeStateProvider<T> · FeedItemRenderer (if)
 │   ├─ FeedLazyColumn (@Composable) · FeedRenderViewModel (@HiltViewModel, Map2개)
 │   └─ di/FeedMultibindsModule         @Multibinds (빈 맵 보장)
 └─ designsystem/
     ├─ theme/TgTheme
     └─ component/TgComponents          TgAvatar · TgSearchField · TgListRow(highlight,badge)
                                        · highlightContains() · TgPrimaryButton · TgTextButtonRow

── transfer-feed 슬라이스 (UI → Domain → Data) ──────────────────────────────────

:ui:transfer-feed                       (UI Layer)
 ├─ TransferFeedRoute                   @Composable (uiState/query collect, param 구성)
 ├─ TransferFeedViewModel               @HiltViewModel
 │      ├ UseCase 2개 + JustSentStore 주입
 │      ├ TransferFeedUiState[ Loading|Loaded(items)|Error ] · TransferFeedEffect
 │      └ rebuild(): 모델(VO) → UiState 변환 + 검색필터 + justSent 병합 + 확장축소
 ├─ uistate/ (: FeedItemUiState)        SearchBarUiState(query) · MyAccountItemUiState
 │      · MyAccountMoreButtonUiState(expanded,hiddenCount)
 │      · RecentAccountItemUiState(justSent) · RecentPhoneItemUiState(justSent) · FeedSectionHeaderUiState
 ├─ state/   TransferFeedStateParam(query,onQueryChange,onClear,onToggle,onSelect×3)
 │      + SearchBarState · MyAccountItemState(query) · MyAccountMoreButtonState(hiddenCount)
 │        · RecentAccountItemState(query) · RecentPhoneItemState(query) · SectionHeaderState
 ├─ item/    SearchBarItem · MyAccountItem · MyAccountMoreButton
 │             · RecentAccountItem(badge) · RecentPhoneItem(badge) · SectionHeader
 ├─ di/ViewTypeStateModule              @IntoMap @ClassKey(UiState)  [계층4]
 ├─ di/FeedRendererModule               @IntoMap @ClassKey(State)    [계층5]
 └─ navigation/TransferFeedEntryProvider  @IntoSet NavEntryProvider

:domain:transfer-feed                   (Domain Layer)
 ├─ usecase/   GetMyAccountsUseCase(Impl) · GetRecentRecipientsUseCase(Impl)
 │               → (data) Repository 인터페이스에 의존, (data) 모델 반환
 └─ di/UseCaseModule                    @Binds usecase

:data:transfer-feed                     (Data Layer, 최하위)
 ├─ model/      MyAccountVO(showInCollapsed) · RecentRecipientVO[ sealed: Account|Phone ]  ← 공개 모델
 ├─ repository/ MyAccountRepository · RecentRecipientRepository            (interface, public)
 │              MyAccountRepositoryImpl · RecentRecipientRepositoryImpl     (impl, internal)
 ├─ service/    MyAccountService / RecentRecipientService (if) ─ Fake* 구현(delay+실패토글)  ← DataSource
 │              MockFailureSwitch @Singleton
 ├─ entity/     MyAccountEntity · RecentRecipientEntity  @Serializable      ← DTO
 ├─ mapper/     Entity → 모델(VO)  (toVO)
 ├─ di/DataModule                       @Binds service/repository
 └─ assets/     my_accounts.json · recent_recipients.json(100건)

── transfer-send 슬라이스 (UI → Domain → Data) ──────────────────────────────────

:ui:transfer-send                       (UI Layer)
 ├─ TransferSendRoute                   @Composable (타입별 헤더 · 금액상한 다이얼로그 · 진행오버레이 · BackHandler)
 ├─ TransferSendViewModel               @HiltViewModel  SendMoneyUseCase + JustSentStore 주입
 │      ├ TransferSendUiState(amount,isSending,showMaxDialog; canSend, amountValue)
 │      ├ const MAX_AMOUNT = 2_000_000
 │      └ onAmountChange / onClickSend(SentRecipient) / onDismissMaxDialog
 └─ navigation/TransferSendEntryProvider  @IntoSet NavEntryProvider

:domain:transfer-send                   (Domain Layer)
 ├─ usecase/SendMoneyUseCase(Impl)      → (data) TransferSendRepository 에 위임
 └─ di/UseCaseModule

:data:transfer-send                     (Data Layer, 최하위)
 ├─ repository/ TransferSendRepository(interface) · TransferSendRepositoryImpl(internal)
 ├─ datasource/ TransferSendDataSource(if) · FakeTransferSendDataSource(delay 10초)
 └─ di/DataModule                       @Binds datasource/repository
```

**관계 요약**
- 레이어 방향: `ui → domain → data` (한 방향). data 가 모델·repository(if+impl)·데이터소스를 모두 소유하는 **최하위**.
- `FeedItemUiState`(core:feed) ◀ 구현 ─ `XxxUiState`(ui/uistate) ◀ 변환 ─ `XxxState`(ui/state) ◀ 렌더 ─ `XxxItem`(ui/item).
- `NavEntryProvider`(core:navigation) ◀ `@IntoSet` ─ 각 feature 의 `*EntryProvider` → `AppNavViewModel` 이 `Set` 으로 수집 → `TgNavHost` 가 등록.
- `JustSentStore`(core:common) 를 송금(write)·피드(read) 두 ViewModel 이 공유해 화면 간 상태를 전달.

---

## 빌드 & 실행

```bash
./gradlew assembleDebug          # 디버그 APK 빌드
./gradlew installDebug           # 연결된 기기/에뮬레이터 설치
```

## 동작 검증 체크리스트 (에뮬레이터 end-to-end)
- **확장/축소**: 기본 축소(showInCollapsed) → `+N개 더보기` → 전체+`접기` → 복귀.
- **검색**: "토" 입력 → 매칭 계좌 노출(숨은 계좌 포함, 더보기/접기 숨김, 매칭부 bold) / 빈 검색 → 전체 복원.
- **방금 송금**: 입금처 선택 → 송금 완료 복귀 → 검색어 초기화 + 해당 입금처 최근 최상단 `[방금 송금]` 뱃지.
- **송금 화면**: account 타입 이름/은행/계좌 · phone 타입 이름/전화번호 · 금액 200만 초과 거부+다이얼로그 · 0/빈값 disable · 10초 진행 중 전체 입력차단.
- **회전**: 검색어/금액/확장상태 유지, API 미재호출.
