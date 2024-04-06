import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("io.realm.kotlin")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("io.github.luca992.multiplatform-swiftpackage") version "2.2.0"
}

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target

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
}

kotlin {
    androidTarget()
    jvm()

    listOf(
        iosArm64(), iosX64(), iosSimulatorArm64(), macosArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "BikeShareKit"
        }
    }

    compilerOptions {
        languageVersion.set(KOTLIN_1_9)
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            api(libs.koin.core)
            implementation(libs.koin.test)

            implementation(libs.bundles.ktor.common)
            implementation(libs.realm)
            api(libs.kmmViewModel)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(Deps.androidXLifecycleViewModel)
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
}

multiplatformSwiftPackage {
    packageName("BikeShareKit")
    swiftToolsVersion("5.9")
    targetPlatforms {
        iOS { v("14") }
        macOS { v("12")}
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

