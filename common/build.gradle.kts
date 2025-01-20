import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kmpNativeCoroutines)
    alias(libs.plugins.room)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    id("io.github.luca992.multiplatform-swiftpackage") version "2.2.3"
}


android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "dev.johnoreilly.bikeshare.common"
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    jvm()

    listOf(
        iosArm64(), iosX64(), iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "BikeShareKit"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            api(libs.bundles.kotlinInject)
            api(libs.circuit.foundation)
            api(libs.circuit.codegen.annotations)

            implementation(libs.bundles.ktor.common)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            api(libs.kmpObservableViewModel)

            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.compose.adaptive)
            implementation(libs.compose.adaptive.layout)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.okhttp.core)
            // workaround for https://youtrack.jetbrains.com/issue/CMP-5959/Invalid-redirect-in-window-core#focus=Comments-27-10365630.0-0
            implementation("androidx.window:window-core:1.3.0")
            implementation(libs.slf4j.android)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.ios)
        }

        jvmMain.dependencies {
            // hack to allow use of MainScope() in shared code used by JVM console app
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${libs.versions.coroutines}")

            implementation(libs.ktor.client.java)
            implementation(libs.slf4j)
        }
    }
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.get("main").kotlinOptions.freeCompilerArgs += "-Xexport-kdoc"
    }

    targets.configureEach {
        val isAndroidTarget = platformType == KotlinPlatformType.androidJvm
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    if (isAndroidTarget) {
                        freeCompilerArgs.addAll(
                            "-P",
                            "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=dev.johnoreilly.common.screens.Parcelize",
                        )
                    }
                }
            }
        }
    }
}

multiplatformSwiftPackage {
    packageName("BikeShareKit")
    swiftToolsVersion("5.9")
    targetPlatforms {
        iOS { v("14") }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    //languageSettings.enableLanguageFeature("ExplicitBackingFields")
}


dependencies {
    ksp(libs.androidx.room.compiler)
    ksp(libs.kotlinInject.compiler)
    ksp(libs.kotlinInject.anvil.compiler)
    ksp(libs.circuit.codegen)
}

room {
    schemaDirectory("$projectDir/schemas")
}

ksp {
    arg("circuit.codegen.lenient", "true")
    arg("circuit.codegen.mode", "kotlin_inject_anvil")
    arg("kotlin-inject-anvil-contributing-annotations", "com.slack.circuit.codegen.annotations.CircuitInject")
}


configurations.configureEach {
    exclude("androidx.window.core", "window-core")
}