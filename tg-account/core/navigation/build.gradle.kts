plugins {
    id("tg.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tglee.tgaccount.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    // RecipientType enum 을 NavKey 필드로 노출하므로 api 로 전파한다(common 이 더 하위, 순환 없음).
    api(project(":core:common"))
    implementation(libs.kotlinx.serialization.json)
}
