import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

private val libs = the<LibrariesForLibs>()

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    "implementation"(platform(libs.androidx.compose.bom))
    "androidTestImplementation"(platform(libs.androidx.compose.bom))

    "implementation"(libs.androidx.compose.ui)
    "implementation"(libs.androidx.compose.ui.graphics)
    "implementation"(libs.androidx.compose.ui.tooling.preview)
    "implementation"(libs.androidx.compose.material3)
    "implementation"(libs.androidx.activity.compose)
    "implementation"(libs.androidx.lifecycle.runtime.compose)
    "implementation"(libs.androidx.lifecycle.viewmodel.compose)
    "debugImplementation"(libs.androidx.compose.ui.tooling)
}
