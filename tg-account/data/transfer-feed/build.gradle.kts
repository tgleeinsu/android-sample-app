plugins {
    id("tg.android.library")
    id("tg.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tglee.tgaccount.data.transferfeed"
}

dependencies {
    // 모델(VO/Entity)·마커는 core:feed 가 소유한다. 공개 API(getMergedViewTypes 의 FeedVO 등)에
    // core:feed 타입이 노출되므로 api 로 전이시킨다.
    implementation(project(":core:common"))
    api(project(":core:feed"))
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
}
