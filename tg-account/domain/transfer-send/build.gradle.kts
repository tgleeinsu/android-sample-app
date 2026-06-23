plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transfersend"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
