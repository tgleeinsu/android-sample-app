plugins {
    id("tg.android.library")
    id("tg.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tglee.tgaccount.data.transferfeed"
}

dependencies {
    implementation(project(":domain:transfer-feed"))
    implementation(project(":core:common"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
}
