plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // 생성된 버전 카탈로그 접근자(LibrariesForLibs)를 precompiled 스크립트 플러그인에서 쓰기 위한 트릭 (android_v2 동일)
    implementation(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))

    // precompiled 스크립트 플러그인이 id 로 적용할 수 있도록 Gradle 플러그인 아티팩트를 클래스패스에 올린다.
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
}
