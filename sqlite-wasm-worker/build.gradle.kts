import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        useEsModules()
    }

    sourceSets {
        wasmJsMain.dependencies {
            api(libs.sqlite.web)
            implementation(libs.kotlinx.browser)
            implementation(
                npm("sqlite-wasm-worker", layout.projectDirectory.dir("worker").asFile)
            )
        }
    }
}
