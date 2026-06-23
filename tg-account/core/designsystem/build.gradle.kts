plugins {
    id("tg.android.library")
    id("tg.android.compose")
}

android {
    namespace = "com.tglee.tgaccount.core.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.material.icons.extended)
}
