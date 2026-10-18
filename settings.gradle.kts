
rootProject.name = "BikeShare"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
        // Kotlin dev builds (2.5.0-Beta2-*) for Swift Export; drop once Beta2 is on Maven Central
        maven("https://packages.jetbrains.team/maven/p/kt/dev")
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // Kotlin dev builds (2.5.0-Beta2-*) for Swift Export; drop once Beta2 is on Maven Central
        maven("https://packages.jetbrains.team/maven/p/kt/dev")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":androidApp")
include(":common")
include(":ios-export")
include(":compose-desktop")
include(":compose-web")
include(":sqlite-wasm-worker")

