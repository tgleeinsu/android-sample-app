plugins {
    id("tg.android.library")
    id("tg.android.compose")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.ui.transferfeed"
}

dependencies {
    implementation(project(":core:feed"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":domain:transfer-feed"))
    implementation(project(":data:transfer-feed"))
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
}
