
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
        //maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":androidApp")
include(":common")
include(":compose-desktop")
include(":compose-web")

