# 송금 샘플 앱 구현 플랜 (android_v2 아키텍처 차용)

## Context

`tg-account` 는 현재 `:app` 단일 모듈만 있는 깨끗한 Compose 프로젝트(Hilt/Navigation/멀티모듈 없음, AGP 9.2.1 · Kotlin 2.2.10 · composeBom 2026.02.01 · minSdk 24 · targetSdk 36). 운영 프로젝트 `android_v2` 의 Clean Architecture·멀티바인딩 피드·Navigation 패턴을 차용해 2화면 송금 샘플 앱을 신규 구축한다. 목적은 요구된 기능 5종(피드 조합 / 회전 안정성 / 멀티바인딩 피드 / 송금 화면 / fake 10초 송금)을 android_v2 의 구조적 패턴 위에서 동작시키는 것이다.

### 전수조사로 확인한 android_v2 핵심
- **3-layer**: `ui → domain(usecase/vo/uistate/repository interface) → data(service/repositoryImpl/entity/mapper)`, 단방향 의존.
- **Hilt**: `@HiltAndroidApp`, `@Binds`(repo/usecase), `@Provides`(service), `@HiltViewModel`, `@IntoMap @ClassKey` 멀티바인딩.
- **멀티바인딩 피드**: `FeedItemUiState → FeedItemState(getKey) → @UniversalItem Composable`. `ViewTypeStateProvider`(UiState→State, `@IntoMap @ClassKey`) + KSP 생성 Provider map(State→Composable). `FeedLazyColumn` 이 `List<FeedItemUiState>` 를 `itemsIndexed` polymorphic 렌더. ViewModel 이 두 API 응답을 한 리스트로 조합.
- **Navigation**: `@Serializable Path` 를 공용 모듈에 두고 `NavGraphProvider` 를 Hilt `@IntoSet` 분산 등록 → gateway 가 수집. ⚠️ android_v2 는 androidx.navigation **2.8.5**. 본 샘플은 요구대로 **Navigation3** 로 변환(컨셉만 차용).
- **상태**: `MutableStateFlow` + sealed `UiState`, `SavedStateHandle.toRoute()` 인자 수신 + init 1회 로드(회전 시 재호출 방지), SideEffect 는 `Channel`/`receiveAsFlow`.

### 확정된 결정 (사용자 승인)
1. 피드: **KSP 없이 Hilt `@IntoMap @ClassKey` 멀티바인딩**으로 단순화(다형 피드·두 단계 등록은 그대로 재현).
2. 빌드: **buildSrc precompiled convention plugin** 도입.
3. 최근보낸계좌 type: **account/phone 2종 뷰타입 분리**(멀티바인딩 다형성 시연 강화).
4. (자체 결정) 아이콘 url 표시는 **Coil** 사용.

---

## 모듈 그래프 (총 11모듈)

```
:app  (@HiltAndroidApp, MainActivity, NavDisplay 호스트, Set<NavEntryProvider> 수집)
  └─ ui:* + data:* + core:*  의존

ui:transfer-feed ─┬─ domain:transfer-feed ──┐
ui:transfer-send ─┴─ domain:transfer-send   ├─ (ui→data implementation: Hilt 모듈 포함)
data:transfer-feed ─ domain:transfer-feed   │
data:transfer-send ─ domain:transfer-send   │
core:feed  (FeedItemUiState/State/Renderer/ViewTypeStateProvider/FeedLazyColumn)  ◄ ui,domain 의존
core:navigation  (NavKey 2종 + NavEntryProvider if)  ◄ ui,app 의존
core:common  (Result, AssetJsonLoader, Dispatcher)  ◄ data,domain 의존
core:designsystem  (Theme + 공통 Composable)  ◄ ui 의존
```

의존 규칙(android_v2 동일): `ui:* → domain:*`(인터페이스), `data:* → domain:*`(RepoImpl 구현), `ui:* → data:*`(implementation, Hilt 모듈 자동 포함). `FeedItemUiState`/`FeedItemState` 인터페이스는 `core:feed`, 구체 UiState 는 각 domain.

---

## 빌드 인프라 (buildSrc)

`buildSrc/src/main/kotlin/` precompiled convention plugin 4종 (android_v2 축소판):
- `tg.android.library` — android-library + kotlin + minSdk24/target36/JDK11 공통.
- `tg.android.hilt` — hilt plugin + ksp + hilt-compiler.
- `tg.android.feature.ui` — android.library + hilt + compose + lifecycle-viewmodel-compose + core:designsystem 의존.
- `tg.kotlin.library` — 순수 kotlin(필요 시). domain 은 UiState `@Stable` 헬퍼 때문에 android-library 채택(android_v2 동일).

`libs.versions.toml` 추가: plugins(`kotlin-android`, `ksp`, `hilt`, `kotlin-serialization`, `android-library`), libraries(`hilt-android`/`hilt-compiler`, `navigation3-runtime`/`navigation3-ui`, `lifecycle-viewmodel-navigation3`, `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `kotlinx-serialization-json`, `kotlinx-coroutines-android`, `coil-compose`). 루트 `build.gradle.kts` 에 plugins apply false.

> ⚠️ **최우선 검증**: AGP 9.2.1 + Kotlin 2.2.10 + KSP2 + Hilt + Navigation3 + composeBom 2026.02.01 호환. 빈 모듈 1개로 조기 빌드 통과 확인.

---

## 핵심 구현 패턴

### 1) 멀티바인딩 피드 (core:feed, KSP 없는 Hilt 멀티바인딩)
- `interface FeedItemUiState`, `interface FeedItemState { fun getKey(): String }`.
- `fun interface FeedItemRenderer { @Composable fun Render(state: FeedItemState) }` (= android_v2 `UniversalFeedItem`).
- `interface ViewTypeStateProvider<in T: FeedItemUiState> { @Composable fun rememberState(uiState: T, param): FeedItemState? }`.
- `@HiltViewModel FeedRenderViewModel` 이 `Map<Class<*>, ViewTypeStateProvider<*>>` + `Map<Class<*>, FeedItemRenderer>`(둘 다 `@JvmSuppressWildcards`) 주입.
- `FeedLazyColumn(items: List<FeedItemUiState>)` → `itemsIndexed` → provider 로 State 변환 → renderer 로 Composable 렌더.
- 각 ui 아이템: `state/`(FeedItemState + `remember…State` 헬퍼), `item/`(@Composable), `di/`(`@IntoMap @ClassKey(XxxUiState::class)` ViewTypeStateProvider [계층4], `@IntoMap @ClassKey(XxxState::class)` FeedItemRenderer [계층5]). 주석으로 "운영은 계층5를 KSP @UniversalItem 자동화" 명시.

### 2) 입금처 선택 피드 조합 (ui:transfer-feed)
`TransferFeedViewModel` 이 두 UseCase(`GetMyAccountsUseCase`, `GetRecentAccountsUseCase`)를 `combine` 해 순서대로 한 리스트 구성:
1. `SearchBarUiState` ×1
2. `MyAccountItemUiState` × (축소 시 `isCollapsedVisible==true` 만 / 펼침 시 전체)
3. `MyAccountMoreButtonUiState` ×1 (펼침/축소 토글, in-screen 상태 — API 재호출 없음)
4. `RecentAccountItemUiState` / `RecentPhoneItemUiState` ×N (type 별 2종 분리)

sealed `TransferFeedUiState { Loading; Loaded(items); Error }`. 실패 시 `Error`(빈 화면) + `Channel<Effect>` 로 `ShowErrorToast`. Route 에서 `LaunchedEffect { effect.collect { Toast } }`, 아이템 선택 시 `onSelect(recipient)` → backStack push.

### 3) Navigation3 (core:navigation + app)
- `@Serializable data object TransferFeedKey : NavKey`, `@Serializable data class TransferSendKey(displayName, bankName, accountNumber) : NavKey`.
- `interface NavEntryProvider { fun EntryProviderBuilder<NavKey>.register(backStack) }`, 각 ui 모듈이 구현 + Hilt `@IntoSet`.
- `:app/TgNavHost`: `rememberNavBackStack(TransferFeedKey)` + `NavDisplay(entryDecorators = listOf(rememberSavedStateNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()), entryProvider { providers.forEach { it.register(backStack) } })`.
- 인자전달: NavKey data class. 복귀: 송금 완료 → `backStack.removeLastOrNull()`.

### 4) 회전 안정성
- **API 재호출 방지**: ViewModel 이 NavEntry 스코프(`rememberViewModelStoreNavEntryDecorator`)라 회전 시 파괴 안 됨 → init 재실행 없음. 디코레이터 2종 등록이 필수.
- **TextField 값 유지**: 송금 금액은 `savedStateHandle.getStateFlow("amount", "")`, `onValueChange = vm::onAmountChange`. process death 까지 복원. 송금 인자는 `savedStateHandle.toRoute<TransferSendKey>()`.

### 5) mock json (data 레이어)
- `data:transfer-feed/src/main/assets/{my_accounts.json, recent_accounts.json}`.
- `core:common/AssetJsonLoader(@ApplicationContext)` + `kotlinx.serialization` 으로 `List<Entity>` 파싱.
- `Fake*Service`(@Provides) → assets 읽기 → `*RepositoryImpl`(@Binds) 가 `*Mapper` 로 Entity→VO. 실패 시뮬레이션 토글 포함.

### 6) fake 10초 송금 (domain:transfer-send + ui)
- `SendMoneyUseCaseImpl.invoke(amount) { delay(10_000L) }` (별도 API 없음).
- `TransferSendViewModel`: `isSending` (SavedStateHandle 기반, 회전 중 유지). `onSendClick`: 중복 차단 → `isSending=true` → useCase → `Effect.NavigateBackToFeed`.
- UI: `isSending` 시 `CircularProgressIndicator` 오버레이 + `Modifier.pointerInput{}` 터치 소비 + TextField/버튼 `enabled=false` + `BackHandler(isSending){}` 로 뒤로가기 차단.

---

## 디렉토리 구조 (주요 파일)

```
buildSrc/src/main/kotlin/{tg.android.library, tg.android.hilt, tg.android.feature.ui, tg.kotlin.library}.gradle.kts
app/src/main/java/com/tglee/tgaccount/{TgAccountApp, MainActivity, navigation/TgNavHost}.kt
core/common/.../{Result, AssetJsonLoader, di/DispatcherModule}.kt
core/designsystem/.../theme/* + component/{TgSearchBar, TgAccountRow, TgPrimaryButton, TgAvatar}.kt
core/navigation/.../{TransferFeedKey, TransferSendKey, NavEntryProvider}.kt
core/feed/.../{FeedItemUiState, FeedItemState, FeedItemRenderer, ViewTypeStateProvider, FeedLazyColumn, FeedRenderViewModel}.kt
domain/transfer-feed/.../{vo/*, usecase/{GetMyAccounts, GetRecentAccounts}*, repository/*, uistate/{SearchBar, MyAccountItem, MyAccountMoreButton, RecentAccountItem, RecentPhoneItem}UiState, di/UseCaseModule}.kt
domain/transfer-send/.../{usecase/SendMoneyUseCase*, di/UseCaseModule}.kt
data/transfer-feed/{src/main/assets/*.json, .../{entity/*, service/Fake*, mapper/*, repository/*Impl, di/{ServiceModule, RepositoryModule}}.kt}
ui/transfer-feed/.../{TransferFeedRoute, TransferFeedViewModel, state/*, item/*, di/{FeedRendererModule, ViewTypeStateModule, NavEntryModule}}.kt
ui/transfer-send/.../{TransferSendRoute, TransferSendViewModel, di/NavEntryModule}.kt
```

---

## 구현 순서
1. **빌드 인프라**: libs.versions.toml + 루트 build.gradle + buildSrc 4 plugin → 빈 모듈로 호환성 조기 검증.
2. **core 모듈**: common → navigation → designsystem(기존 app/ui/theme 이전) → feed.
3. **transfer-feed 수직 슬라이스**: domain → data(assets/Fake/Mapper/RepoImpl/DI) → ui(state/item/Renderer·ViewTypeState 모듈/ViewModel combine+effect/Route).
4. **transfer-send 수직 슬라이스**: domain(delay) → ui(SavedStateHandle/프로그래스·입력차단·BackHandler).
5. **app 조립**: HiltApp, MainActivity, TgNavHost(NavDisplay + 디코레이터 2 + Set 수집).

## 검증 (end-to-end)
- 빌드: `./gradlew assembleDebug` (Apple Silicon 이슈 시 관련 플래그).
- 기능: ① 피드 순서(검색바→내계좌3→더보기→최근계좌 account/phone), ② 더보기 토글 시 API 미재호출, ③ 아이템 선택→송금 인자 전달, ④ **회전**: 피드 API 미재호출 + 금액 입력값 유지, ⑤ 송금 10초 프로그래스/전체 입력차단/완료 후 피드 복귀, ⑥ API 실패 토글 → 토스트 + 빈 화면.
- 멀티바인딩 검증: 새 뷰타입 추가 시 `@IntoMap` 2개(ViewTypeStateProvider + FeedItemRenderer)만으로 등록되는지.

## 주요 리스크
- AGP 9.2.1 + KSP2 + Hilt + Navigation3 버전 호환(1단계 조기 검증, 비호환 시 버전 조정 우선).
- Navigation3 ViewModel 스코핑: 디코레이터 미등록 시 회전/pop 동작 깨짐 → `rememberViewModelStoreNavEntryDecorator` + `rememberSavedStateNavEntryDecorator` 필수.

## android_v2 참고 파일
- `blind-common-ui/.../feed/FeedLazyColumn.kt`, `.../viewmodel/ViewTypeStateViewModel.kt`
- `ui/alter/.../di/AlterViewTypeStateProviderModule.kt` (@IntoMap @ClassKey 예시)
- `feature-article/.../viewmodel/PromotionLinkInputViewModel.kt` (SavedStateHandle)
- `blind-common-ui/.../NavGraphProvider.kt` (분산 등록 컨셉)