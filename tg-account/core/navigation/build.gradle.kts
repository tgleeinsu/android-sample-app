plugins {
    id("tg.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tglee.tgaccount.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    api(project(":core:common"))
    implementation(libs.kotlinx.serialization.json)
}
