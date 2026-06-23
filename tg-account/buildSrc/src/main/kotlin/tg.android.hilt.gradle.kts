import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

private val libs = the<LibrariesForLibs>()

dependencies {
    "implementation"(libs.hilt.android)
    "ksp"(libs.hilt.compiler)
}
