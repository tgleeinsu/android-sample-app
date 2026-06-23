pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-sample-app-tg-account"
include(":app")

// Core
include(":core:common")
include(":core:designsystem")
include(":core:navigation")
include(":core:feed")

// Feature: transfer-feed (입금처 선택)
include(":domain:transfer-feed")
include(":data:transfer-feed")
include(":ui:transfer-feed")

// Feature: transfer-send (송금)
//include(":domain:transfer-send")
//include(":ui:transfer-send")
