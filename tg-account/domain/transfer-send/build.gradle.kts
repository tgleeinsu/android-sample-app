plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transfersend"
}

dependencies {
    // 도메인(use case)이 데이터 레이어의 repository 인터페이스에 의존한다 (domain → data).
    implementation(project(":data:transfer-send"))
    implementation(libs.kotlinx.coroutines.android)
}
