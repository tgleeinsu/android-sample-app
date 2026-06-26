plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transferfeed"
}

dependencies {
    // 도메인(use case)이 데이터 레이어의 repository 인터페이스에 의존한다 (domain → data).
    // use case 가 data 의 모델을 반환하므로 api 로 전파해 UI 가 함께 볼 수 있게 한다.
    api(project(":data:transfer-feed"))
    implementation(libs.kotlinx.coroutines.android)
}
