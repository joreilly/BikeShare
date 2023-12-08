import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatform
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
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
        @OptIn(ExperimentalComposeLibrary::class)
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)

                implementation("io.ktor:ktor-client-core:3.0.0-wasm1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm1")
                implementation("io.ktor:ktor-client-content-negotiation:3.2.0")
            }
        }
    }
}

compose.experimental {
    web.application {}
}
