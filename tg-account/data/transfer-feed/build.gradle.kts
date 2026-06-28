plugins {
    id("tg.android.library")
    id("tg.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tglee.tgaccount.data.transferfeed"
}

dependencies {
    implementation(project(":core:common"))
    api(project(":core:feed"))
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
}
