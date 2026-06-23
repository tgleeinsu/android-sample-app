plugins {
    id("tg.android.library")
    id("tg.android.compose")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.core.feed"
}

dependencies {
    implementation(libs.hilt.navigation.compose)
}
