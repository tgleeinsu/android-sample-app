plugins {
    id("tg.android.library")
    id("tg.android.hilt")
}

android {
    namespace = "com.tglee.tgaccount.domain.transferfeed"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:feed"))
    api(project(":data:transfer-feed"))
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.coroutines.android)
}
