plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        moduleName = "bikeshare"
        browser {
            commonWebpackConfig {
                outputFileName = "bikeshare.js"
            }
        }
        binaries.executable()
//        applyBinaryen()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)
            }
        }
    }
}
