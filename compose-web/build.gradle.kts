plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "bikeshare.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation(projects.common)
        }
    }
}
