plugins {
    id("tg.android.library")
    id("tg.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tglee.tgaccount.data.transferfeed"
}

dependencies {
    // 데이터 레이어가 최하위: 모델/리포지토리 인터페이스를 직접 소유하므로 domain 에 의존하지 않는다.
    implementation(project(":core:common"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
}
