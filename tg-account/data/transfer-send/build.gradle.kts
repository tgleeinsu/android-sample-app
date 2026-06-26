plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.data.transfersend"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
