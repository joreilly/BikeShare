@file:OptIn(ExperimentalSwiftExportDsl::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.export.SwiftExportVisibility
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

// Umbrella for Swift Export's `export { swift { } }` DSL: :common (an `api` dep here) is exported,
// along with whatever :common exposes via `api`. Libraries Swift doesn't use directly are HIDDEN,
// which cuts them down to stubs for the few types :common's API mentions. A function returning a
// hidden type is generated as a fatalError() stub with no warning, so check the generated Swift.
plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.common)
        }
    }

    export {
        swift {
            moduleName = "BikeShareKit"
            xcodeIntegration {
                configure(projects.common) {
                    moduleName = "Common"
                    rootPackage = "dev.johnoreilly.common"
                }
                // Everything :common's public API drags in that Swift doesn't use (Circuit, Compose,
                // Ktor, Room, ...). Only the stdlib and coroutines (for StateFlow) stay exposed.
                listOf(
                    "me.tatarka.inject:kotlin-inject-runtime",
                    "com.slack.circuit:circuit-foundation",
                    "com.slack.circuit:circuit-runtime",
                    "com.slack.circuit:circuit-runtime-navigation",
                    "com.slack.circuit:circuit-runtime-screen",
                    "org.jetbrains.compose.runtime:runtime",
                    "org.jetbrains.compose.ui:ui-graphics",
                    "org.jetbrains.androidx.lifecycle:lifecycle-viewmodel",
                    "androidx.room3:room3-runtime",
                    "androidx.sqlite:sqlite",
                    "io.ktor:ktor-client-core",
                    "io.ktor:ktor-events",
                    "io.ktor:ktor-http",
                    "io.ktor:ktor-io",
                    "io.ktor:ktor-utils",
                    "org.jetbrains.kotlinx:kotlinx-io-core",
                    "org.jetbrains.kotlinx:kotlinx-serialization-core",
                    "org.jetbrains.kotlinx:kotlinx-serialization-json",
                    "ovh.plrapps:mapcompose-mp",
                ).forEach { configure(it, SwiftExportVisibility.HIDDEN) }
            }
        }
    }
}
