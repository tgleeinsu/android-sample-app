plugins {
    id("tg.android.library")
    id("tg.android.compose")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.ui.transfersend"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":domain:transfer-send"))
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
}
