plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kmpNativeCoroutines)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    //alias(libs.plugins.realm.kotlin)
    id(libs.plugins.realm.kotlin.get().pluginId)
    id("io.github.luca992.multiplatform-swiftpackage") version "2.2.2"
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
        iosArm64(), iosX64(), iosSimulatorArm64(), macosArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "BikeShareKit"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            api(libs.kotlininject.runtime)

            implementation(libs.bundles.ktor.common)
            implementation(libs.realm)
            api(libs.kmpObservableViewModel)

            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
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
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    languageSettings.enableLanguageFeature("ExplicitBackingFields")
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

dependencies {
    add("kspAndroid", libs.kotlininject.compiler)
    add("kspIosX64", libs.kotlininject.compiler)
    add("kspIosArm64", libs.kotlininject.compiler)
    add("kspIosSimulatorArm64", libs.kotlininject.compiler)
    add("kspJvm", libs.kotlininject.compiler)
}
