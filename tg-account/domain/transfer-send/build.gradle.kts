plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transfersend"
}

dependencies {
    implementation(project(":data:transfer-send"))
    implementation(libs.kotlinx.coroutines.android)
}
