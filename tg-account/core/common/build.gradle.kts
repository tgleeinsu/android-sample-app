plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
