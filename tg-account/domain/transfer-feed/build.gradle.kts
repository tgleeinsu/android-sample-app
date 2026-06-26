plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transferfeed"
}

dependencies {
    api(project(":data:transfer-feed"))
    implementation(libs.kotlinx.coroutines.android)
}
