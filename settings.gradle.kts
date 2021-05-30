pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

}

rootProject.name = "BikeShare"

include(":app", ":common", ":compose-desktop")

