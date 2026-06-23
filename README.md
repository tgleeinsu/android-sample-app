# android-sample-app

여러 가지 Android 샘플 앱을 모아두는 모노레포입니다. 각 샘플 앱은 독립적인
Gradle 프로젝트로, 자체 `gradlew` / `settings.gradle.kts` / `app/` 모듈을 가집니다.
공통 `.gitignore` 하나로 모든 하위 프로젝트의 빌드 산출물을 무시합니다.

## 구조

```
android-sample-app/
├── README.md
├── .gitignore          ← 루트 공통 (모든 build/, .gradle/, local.properties, key 등등 무시)
└── tg-account/         ← 샘플 앱 1 (Jetpack Compose)
    ├── gradlew
    ├── settings.gradle.kts
    ├── gradle/libs.versions.toml
    └── app/
```

## 샘플 앱 목록

| 앱 | 설명                            | 스택                         |
| --- |-------------------------------|----------------------------|
| [tg-account](./tg-account) |                         |                    |

## 새 샘플 앱 추가하기

1. Android Studio → **New Project** → **Empty Activity (Compose)** 선택
2. **Save location** 을 이 저장소 안의 새 폴더로 지정 (예: `android-sample-app/ProjectB`)
3. 생성하면 해당 폴더 안에 `gradlew`, `settings.gradle.kts`, `app/` 등이 자동 생성됨
4. `.gitignore` 는 루트 것 하나를 공유하므로 별도 설정 불필요

## 빌드 / 실행

각 샘플 앱 폴더로 이동해서 해당 앱의 wrapper 로 빌드합니다.

```bash
cd tg-account
./gradlew assembleDebug      # 디버그 APK 빌드
./gradlew installDebug       # 연결된 기기/에뮬레이터에 설치
```

> 각 프로젝트는 독립 Gradle 빌드이므로, 루트에는 settings.gradle 이 없습니다.
> Android Studio 에서 열 때는 모노레포 루트가 아니라 개별 샘플 앱 폴더(`tg-account` 등)를 여세요.
