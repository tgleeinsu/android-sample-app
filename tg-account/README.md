# TG Account — 송금 샘플 앱 설명서

## 0. 설명서

**Google 권장 아키텍처(Modern App Architecture)** 기반의 **2화면 송금 샘플 앱**.
입금처를 고르는 **피드 화면**과, 금액을 입력해 보내는 **송금 화면**을 11개 멀티모듈 위에 올렸다.
피드는 운영 앱의 **서버 드리븐 동적 피드**를 KSP 없이 **Hilt 멀티바인딩**으로 축소 재현한 것이 핵심이다.

- AGP 9.2.x · Kotlin 2.2.x · Compose BOM 2026.02 · minSdk 24 / targetSdk 36 / compileSdk 37
- 빌드 인프라: `buildSrc` precompiled convention plugin 3종 — `tg.android.library` · `tg.android.compose` · `tg.android.hilt`
- Navigation3(`androidx.navigation3`) + Hilt 멀티바인딩으로 화면·피드아이템을 **모듈별 분산 등록**

> **아키텍처 한 줄 요약** — 의존이 **UI → Domain → Data** 한 방향으로만 내려간다.
> repository **인터페이스와 구현이 모두 Data 레이어**에 있고(데이터 레이어의 공개 API), Domain 의 UseCase 가 그 인터페이스에 의존한다.
> (repo 인터페이스를 Domain 에 두는 의존성 역전형 Clean Architecture 와 구분되는 지점.)

> ### ⚠️ 현재 마이그레이션 상태 (정직한 표기)
> 이 앱은 **옛 명령형 조립(`TransferFeedViewModel.rebuild()`) → 새 서버 드리븐 피드 파이프라인** 으로 이전 중이다.
> - ✅ 완료: 피드 5계층 파이프라인(모델을 `core:feed` 로 이동), 반응형 UseCase, 에러의 **일회성 이벤트화**(Toast), 멀티바인딩 등록.
> - 🚧 미완: **검색 하이라이트 · 더보기 확장/축소 · "방금 송금" 뱃지** 의 동적 조립. 현재 `TransferFeedStateParam` 은 `// TODO` 스텁이고, repository 는 더보기/헤더를 **기본값으로 무조건 주입**한다. → 조립 전담 UseCase 도입이 다음 단계.
> - 🗑️ 참고용(dead): `TransferFeedRoute.kt`, `vm/TransferFeedViewModel.kt` — 옛 명령형 방식의 잔재(주석/비활성).

---

## 1. 전체 워크플로우 (런타임 흐름)

사용자 입력 → UI → ViewModel → UseCase → Repository → Service(Fake) → assets JSON 으로 내려갔다가,
`Entity → VO → UiState` 로 매핑돼 리스트로 조합되고, 멀티바인딩 피드를 통해 다시 화면으로 올라온다.

```
┌────────────────────────────────────────────────────────────────────────────────┐
│  MainActivity (@AndroidEntryPoint) → TgTheme → TgNavHost                          │
│    NavDisplay( backStack = rememberNavBackStack(TransferFeedKey),                 │
│               entryProvider = AppNavViewModel.entryProviders.forEach{ register } )│
└───────────────┬───────────────────────────────────────────┬─────────────────────┘
        TransferFeedKey                                TransferSendKey
                │                                              │
   ┌────────────▼─────────────┐   아이템 선택        ┌────────────▼──────────────┐
   │ [화면 A] 입금처 선택 피드   │ ─TransferSendKey──▶ │ [화면 B] 송금               │
   │  TransferScreen (Compose) │ (recipientId,name) │  TransferSendRoute (Compose)│
   │  rememberTransferScreenState                   │  collect uiState / effect  │
   └────────────┬─────────────┘                     └────────────┬─────────────┘
                │ hiltViewModel()                                │ hiltViewModel()
   ┌────────────▼─────────────┐                     ┌────────────▼─────────────┐
   │  TransferScreenViewModel  │                     │  TransferSendViewModel    │
   │   observe().stateIn       │                     │   onClickSend()           │
   │   observeEvent()→Toast    │                     │     │                     │
   └────────────┬─────────────┘                     │     ▼                     │
                │ observe()                          │  SendMoneyUseCase (domain)│
   ┌────────────▼──────────────────────┐            │     │                     │
   │ GetTransferScreenUiStateUseCase    │            │     ▼                     │
   │  combine(loading, items)→ScreenState            │  TransferSendRepository   │
   │  실패 → UiEvent.ShowErrorToast(채널)│            │   (data, interface+Impl)  │
   └────────────┬──────────────────────┘            │     │                     │
                │ observe()                          │     ▼                     │
   ┌────────────▼─────────────┐                      │  FakeTransferSendDataSrc  │
   │  LoadTransferFeedUseCase  │                      │   delay(3초) (fake)       │
   │   VO → UiState 매핑        │                      │     │ 완료 → Effect       │
   └────────────┬─────────────┘                      │     ▼  NavigateBackToFeed │
                │ getMergedViewTypes(): Flow<List<FeedVO>>    └──────┬───────────┘
   ┌────────────▼─────────────┐                                     │ backStack.pop
   │ TransferFeedViewTypeRepository (data)                          │
   │  combine(myAccounts, recents) + searchBar/더보기/헤더 기본값 주입 │
   └────────────┬─────────────┘                                     ▼
                │                                          [화면 A] 복귀
   ┌────────────▼─────────────┐
   │ MyAccountService / RecentRecipientService (Fake, delay)          │
   │   → AssetJsonLoader → assets/my_accounts.json·recent_recipients.json
   └──────────────────────────┘
```

**핵심 흐름 포인트**
- **단방향 데이터 흐름**: 데이터가 바뀌면 `Flow.combine → stateIn` 으로 자동 갱신. UI 는 `collectAsStateWithLifecycle` 로 구독만 한다.
- **에러 = 일회성 이벤트**: 로드 실패는 상태(state)가 아니라 `Channel` 기반 `observeEvent()` 로 흘러나오고, UI 가 `LaunchedEffect(Unit)` 으로 collect 해 **Toast 한 번** 띄운다(회전·재구성에 재발화 없음). `TransferScreenUiState` 에는 Error 가 없다(Loading/Loaded 뿐).
- **송금 → 복귀(SideEffect)**: 송금 완료 시 `TransferSendEffect.NavigateBackToFeed`(Channel) → `onSendComplete()` → `backStack.removeLastOrNull()`.
- **회전 안정성**: ViewModel 이 NavEntry 스코프라 회전 시 파괴되지 않아 `observe()` 구독·입력값이 유지된다.

---

## 2. 모듈 간 의존성 관계 (11개 모듈)

**Google Modern App Architecture**: 컴파일 의존이 `UI → Domain → Data` 로 **한 방향**. **Data 가 최하위**(다른 feature 를 전혀 모름)다. 화살표 `→` 는 "의존한다".

```
                              ┌─────────┐
                              │  :app   │  @HiltAndroidApp · NavDisplay 호스트
                              └────┬────┘
        ┌──────────────┬──────────┼───────────┬──────────────────┐
        ▼              ▼          ▼            ▼                  ▼
 :ui:transfer-feed  :ui:transfer-send  :core:feed   :core:designsystem  :core:navigation
   (UI)               (UI)             (피드 계약+모델) (Compose 컴포넌트)  (Nav 계약)
        │                  │
        ▼                  ▼
 :domain:transfer-feed  :domain:transfer-send        (Domain: UseCase)
        │  api               │
        ▼                    ▼
 :data:transfer-feed    :data:transfer-send          (Data: 최하위 · repo if+impl)
        │  api(core:feed)
        ▼
 :core:feed                  :core:navigation ──api──▶ :core:common
 :core:common / :core:designsystem                    (최하위 공용)
```

**레이어 분류**

| 레이어 | 모듈 | 역할 |
|--------|------|------|
| **UI** | `:ui:transfer-feed` · `:ui:transfer-send` (+`:app` 호스트) | Compose 화면 · ViewModel · 화면 State · 피드 아이템 등록 |
| **Domain** | `:domain:transfer-feed` · `:domain:transfer-send` | UseCase (data repository 인터페이스에 의존) |
| **Data** | `:data:transfer-feed` · `:data:transfer-send` | repository(인터페이스+구현) · Service/DataSource(Fake) |
| **Core** | `:core:common` · `:core:designsystem` · `:core:navigation` · `:core:feed` | 레이어 공통 기반 |

**의존성 표 (project 의존만, 검증값)**

| 모듈 | 의존 대상 |
|------|-----------|
| `:app` | `:core:navigation` · `:core:designsystem` · `:core:feed` · `:ui:transfer-feed` · `:ui:transfer-send` |
| `:ui:transfer-feed` | `:core:feed` · `:core:designsystem` · `:core:navigation` · `:domain:transfer-feed` |
| `:ui:transfer-send` | `:core:common` · `:core:designsystem` · `:core:navigation` · `:domain:transfer-send` |
| `:domain:transfer-feed` | `:core:common` · `:core:feed` · **api** `:data:transfer-feed` |
| `:domain:transfer-send` | `:data:transfer-send` |
| `:data:transfer-feed` | `:core:common` · **api** `:core:feed` (※ domain 에 의존하지 않음) |
| `:data:transfer-send` | (project 의존 없음 — coroutines 만) |
| `:core:navigation` | **api** `:core:common` |
| `:core:feed` · `:core:designsystem` · `:core:common` | (project 의존 없음) |

**규칙·검증 결과**
- `ui → domain → data` 단방향. **역방향(data→domain) 0건** — Data 가 최하위라 의존성 역전이 없다(Clean 과 구분점).
- **도메인 순수성 ✅**: domain 모듈에 `android.*`/`androidx.*` import 0건.
- **순환 의존성 없음**(그래프가 DAG). repository 인터페이스+구현이 모두 data 에 있고, UseCase 가 그 인터페이스를 호출.
- `domain:transfer-feed` 가 `api(data:...)` 인 이유: Hilt 가 data 의 repo 를 UseCase 에 주입하려면 data 가 classpath 에 있어야 함. 단 **public API 로 data 타입을 노출하진 않으므로 `implementation` 으로 낮춰도 됨**(자매 모듈 `domain:transfer-send` 는 이미 `implementation`).

---

## 3. 멀티바인딩 피드 워크플로우 (모듈 포함)

서버 드리븐 다형 피드를 **KSP 없이 Hilt `@IntoMap @ClassKey` 두 단계 맵**으로 구성. 새 뷰타입 추가 = 모델 3계층 + `@IntoMap` 2개 등록.

```
[data] TransferFeedViewTypeRepository.getMergedViewTypes(): Flow<List<FeedVO>>
        │  Service(Fake)→Entity, FeedEntityToVOMapper(Entity→VO), searchBar/더보기/헤더 기본 VO 주입
        ▼
[domain] LoadTransferFeedUseCase.observe(): Flow<List<FeedUiState>>
        │  FeedVOToUiStateMapper (VO → UiState)            ← 계층3
        ▼
[domain] GetTransferScreenUiStateUseCase.observe(): Flow<TransferScreenUiState>
        │  combine(loading, items) → Loading | Loaded(items)
        ▼
[ui] TransferScreenViewModel.uiState (stateIn)  +  eventChannel(observeEvent)
        ▼
[ui] TransferScreen → FeedLazyColumn( items, param = TransferFeedStateParam )
        │  items(key = uiState.id, contentType = uiState::class)
        ▼
[core:feed] FeedRenderViewModel  (Hilt 가 두 맵 주입)
        │  ① rememberState(uiState, param)
        │     provider = stateProviders[uiState::class.java]                  ← 계층4
        │     provider.rememberState(...) ─▶ FeedItemState        (@IntoMap @ClassKey(XxxUiState))
        │  ② rendererFor(state)
        │     renderer = renderers[state::class.java]                         ← 계층5
        │     renderer.Render(state) ─▶ @Composable               (@IntoMap @ClassKey(XxxState))
        ▼
   행 렌더: TgSearchField · TgListRow(highlight,badge) · TgTextButtonRow · Text(헤더)
```

**계층·모듈 배치**

| 계층 | 산출물 | 위치(모듈) | 등록 |
|---|---|---|---|
| 1 Entity | `Feed*Entity` | `core:feed/feedmodel/entity` | — |
| 2 VO | `Feed*VO` | `core:feed/feedmodel/vo` | `FeedEntityToVOMapper`(core:feed) |
| 3 UiState | `Feed*UiState : FeedUiState` | `core:feed/feedmodel/uiState` | `FeedVOToUiStateMapper`(core:feed) |
| 4 State | `Feed*State : FeedItemState` | `ui:transfer-feed/feeditem/state` | `ViewTypeStateModule` `@IntoMap @ClassKey(UiState)` |
| 5 Composable | `*Item` | `ui:transfer-feed/feeditem` | `FeedRendererModule` `@IntoMap @ClassKey(State)` |

**키 포인트**
- **계약·모델·매퍼는 `core:feed`** 에 모여 있다: 마커(`FeedEntity`/`FeedVO`/`FeedUiState`/`FeedItemState`/`ViewTypeStateProvider`/`FeedItemRenderer`) + `FeedLazyColumn` + `FeedRenderViewModel` + 두 매퍼 + `@Multibinds`(빈 맵 보장).
- **두 맵의 키가 다르다**: 계층4 는 `UiState::class`, 계층5 는 `State::class`. `FeedRenderViewModel` 이 둘을 잇는다.
- **계층4 에 화면 콜백 주입**: `FeedItemStateParam = TransferFeedStateParam`(검색어·클릭 람다). provider 가 `param` 을 받아 State 에 바인딩.
- **6 뷰타입**: SearchBar · MyAccount · MyAccountMoreButton · RecentAccount · RecentPhone · SectionHeader. (SearchBar/MoreButton/SectionHeader 는 서버가 안 주지만 검색바와 동일하게 **"서버에서 온 것처럼"** 풀 파이프라인을 태운다 — repository 가 기본값 VO 로 주입.)
- **조용한 누락 주의**: 키 미스 시 크래시 없이 그 아이템만 안 그려진다 → 등록 2개(계층4·5)가 반드시 짝이어야 한다.

---

## 4. 클래스 구조 관계성 트리

```
:app  (UI 호스트)
 ├─ TgAccountApp                          @HiltAndroidApp
 ├─ MainActivity                          @AndroidEntryPoint → TgTheme → TgNavHost
 └─ navigation/
     ├─ TgNavHost()                       NavDisplay( rememberNavBackStack(TransferFeedKey),
     │                                                entryProviders.forEach{ register } )
     └─ AppNavViewModel                   @HiltViewModel · Set<@JvmSuppressWildcards NavEntryProvider> 주입

:core
 ├─ common/                               ◀ 최하위 공용
 │   ├─ result/AppResult<T> (sealed)      Success|Failure · onSuccess/onFailure/runCatchingResult
 │   ├─ asset/AssetJsonLoader             @Singleton · assets JSON → @Serializable (Dispatchers.IO)
 │   └─ di/CommonModule(Json) · CoroutineScopeModule(@ApplicationScope)
 ├─ navigation/
 │   ├─ NavKeys: TransferFeedKey(data object) · TransferSendKey(recipientId,name,subtitle)  : NavKey
 │   └─ NavEntryProvider (interface)      register(scope, backStack)
 ├─ feed/                                 ◀ 멀티바인딩 피드 프레임워크(계약+모델+매퍼)
 │   ├─ marker/  FeedEntity · FeedVO · FeedUiState(id) · FeedItemState(getKey)
 │   │           · FeedItemStateParam · ViewTypeStateProvider<T> · FeedItemRenderer
 │   ├─ FeedLazyColumn(@Composable) · FeedRenderViewModel(@HiltViewModel · Map 2개)
 │   ├─ feedmodel/entity|vo|uiState/      Feed{MyAccount,RecentRecipient,TransferSearchBar,
 │   │                                         MyAccountMoreButton,SectionHeader}{Entity|VO|UiState}
 │   │                                    (+ *List, NotSupported*) · RecentRecipient 은 sealed(Account|Phone|None)
 │   ├─ mapper/  FeedEntityToVOMapper(Impl) · FeedVOToUiStateMapper(Impl)
 │   └─ di/      MapperModule(@Binds) · FeedMultiBindsModule(@Multibinds 빈 맵)
 └─ designsystem/
     ├─ theme/TgTheme
     └─ component/TgComponents            TgAvatar · TgSearchField · TgListRow(highlight,badge)
                                          · TgPrimaryButton · TgTextButtonRow

── transfer-feed 슬라이스 (UI → Domain → Data) ──────────────────────────────────

:ui:transfer-feed
 ├─ TransferScreen(@Composable)           Scaffold · when(Loaded→FeedLazyColumn / Loading→Progress)
 ├─ TransferScreenState · rememberTransferScreenState()
 │      screenUiState collect + eventChannel→Toast + TransferFeedStateParam 구성(검색/토글은 TODO)
 ├─ vm/TransferScreenViewModel            @HiltViewModel · observe().stateIn · observeEvent()→eventChannel
 ├─ feeditem/state/  TransferFeedStateParam(검색어+콜백) · Feed{SearchBar,MyAccountItem,
 │                       MyAccountMoreButton,RecentAccountItem,RecentPhoneItem,SectionHeader}State
 ├─ feeditem/        SearchBarItem · MyAccountItem · MyAccountMoreButton
 │                       · RecentAccountItem · RecentPhoneItem · SectionHeader
 ├─ di/ViewTypeStateModule               @IntoMap @ClassKey(UiState)  [계층4]
 ├─ di/FeedRendererModule                @IntoMap @ClassKey(State)    [계층5]
 ├─ di/FeedNavModule                     @Binds @IntoSet NavEntryProvider
 ├─ navigation/TransferFeedEntryProvider  entry<TransferFeedKey>{ TransferScreen } · 선택→backStack.add
 └─ (dead) TransferFeedRoute · vm/TransferFeedViewModel   ← 옛 명령형 rebuild() 잔재(참고용)

:domain:transfer-feed
 ├─ usecase/GetTransferScreenUiStateUseCase(Impl)
 │      observe(): combine(loading, items)→TransferScreenUiState · observeEvent(): UiEvent.ShowErrorToast
 ├─ usecase/LoadTransferFeedUseCase(Impl)  observe(): repo.getMergedViewTypes().map{ VO→UiState }
 ├─ screenuistate/TransferScreenUiState   sealed: Loading | Loaded(items)   (Error 없음)
 └─ di/UseCaseModule                      @InstallIn(ViewModelComponent) @Binds

:data:transfer-feed                       (최하위)
 ├─ repository/TransferFeedViewTypeRepository(interface) · ...Impl(internal)
 │      getMergedViewTypes(): combine(myAccounts, recents) + searchBar/더보기/헤더 기본 VO 주입
 ├─ service/  MyAccountService · RecentRecipientService (if) ─ Fake* (delay) · MockFailureSwitch(@Singleton)
 ├─ di/       ServiceModule(@Binds) · RepositoryModule(@Binds, internal)
 └─ assets/   my_accounts.json · recent_recipients.json
     (Entity/VO/UiState·매퍼는 core:feed 소유)

── transfer-send 슬라이스 (UI → Domain → Data) ──────────────────────────────────

:ui:transfer-send
 ├─ TransferSendRoute(@Composable)        금액 입력 · 진행 오버레이 · BackHandler · effect collect
 ├─ TransferSendViewModel                 @HiltViewModel · SendMoneyUseCase 주입
 │      ├ TransferSendUiState(amount,isSending; canSend,amountValue)
 │      └ TransferSendEffect.NavigateBackToFeed (Channel) · onClickSend()
 └─ navigation/TransferSendEntryProvider  entry<TransferSendKey>{ TransferSendRoute } · @Binds @IntoSet
     (+ TransferSendNavModule)            완료→onSendComplete→backStack.removeLastOrNull()

:domain:transfer-send
 ├─ usecase/SendMoneyUseCase(Impl)        invoke(amount) → TransferSendRepository.sendMoney
 └─ di/UseCaseModule                      @InstallIn(SingletonComponent) @Binds

:data:transfer-send                       (최하위)
 ├─ repository/TransferSendRepository(interface) · ...Impl(internal)
 ├─ datasource/TransferSendDataSource(if) · FakeTransferSendDataSource  delay(SEND_DELAY_MS=3초)
 └─ di/DataModule                         @Binds(internal) datasource/repository
```

**관계 요약**
- 레이어 방향 `ui → domain → data` (한 방향). data 가 repository(if+impl)·Service/DataSource 를 모두 소유하는 **최하위**.
- 피드 한 줄: `FeedUiState`(core:feed) ◀구현─ `Feed*UiState` ◀변환(계층4)─ `Feed*State` ◀렌더(계층5)─ `*Item`.
- `NavEntryProvider`(core:navigation) ◀`@IntoSet`─ 각 feature `*EntryProvider` → `AppNavViewModel` 이 `Set` 수집 → `TgNavHost` 등록.
- 화면 전환은 콜백/Effect 로만: 선택 → `backStack.add(TransferSendKey)`, 송금 완료 → `NavigateBackToFeed` → `backStack.removeLastOrNull()`.

---

## 빌드 & 실행

```bash
./gradlew assembleDebug          # 디버그 APK 빌드
./gradlew installDebug           # 연결된 기기/에뮬레이터 설치
./gradlew compileDebugKotlin     # 전 모듈 컴파일 검증
```

## 동작 검증 체크리스트 (현재 구현 기준)
- **피드 로드**: 앱 시작 → `입금처 선택` → 내 계좌 + 최근 보낸 계좌 리스트 표시(assets JSON, mock 지연).
- **로드 실패**: `MockFailureSwitch` 토글 시 에러 **Toast 1회**(상태 화면은 그대로 — 일회성 이벤트).
- **송금**: 아이템 선택 → 송금 화면(이름) → 금액 입력 후 송금 → 3초 진행 → 자동 복귀.
- **회전**: 입력값·구독 유지, 데이터 미재호출.

> 🚧 **미구현(다음 단계)**: 검색 하이라이트 · 더보기 확장/축소 · "방금 송금" 뱃지 는 조립 전담 UseCase 도입 후 활성화 예정.
> 컴포넌트(`TgSearchField`·`TgListRow(highlight,badge)`)와 모델(`justSent` 등)은 준비되어 있고, 조립 로직만 배선하면 된다.
