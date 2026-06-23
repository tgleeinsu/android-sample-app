plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transferfeed"
}

dependencies {
    implementation(project(":core:common"))
    api(project(":core:feed"))
    implementation(libs.kotlinx.coroutines.android)
}
