plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("io.realm.kotlin")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
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


// CocoaPods requires the podspec to have a version.
version = "1.0"

kotlin {
    targets {
        val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget = when {
            System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
            System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
            else -> ::iosX64
        }
        iosTarget("iOS") {

            binaries.framework {
                baseName = "common"

                // re. https://youtrack.jetbrains.com/issue/KT-60230/Native-unknown-options-iossimulatorversionmin-sdkversion-with-Xcode-15-beta-3
                // due to be fixed in Kotlin 1.9.10
                if (System.getenv("XCODE_VERSION_MAJOR") == "1500") {
                    linkerOpts += "-ld64"
                }
            }
        }

        androidTarget()
        jvm()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Deps.Ktor) {
                    implementation(clientCore)
                    implementation(clientJson)
                    implementation(clientLogging)
                    implementation(clientSerialization)
                    implementation(contentNegotiation)
                    implementation(json)
                }

                with(Deps.Kotlinx) {
                    implementation(coroutinesCore)
                    implementation(serializationCore)
                }

                // Realm
                implementation(Deps.realm)

                // koin
                with(Deps.Koin) {
                    api(core)
                    api(test)
                }

                api("com.rickclephas.kmm:kmm-viewmodel-core:${Versions.kmmViewModel}")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientAndroid)
                implementation(Deps.androidXLifecycleViewModel)
            }
        }

        val iOSMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientIos)
            }
        }
        val iOSTest by getting {
        }

        val jvmMain by getting {
            dependencies {
                // hack to allow use of MainScope() in shared code used by JVM console app
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${Versions.kotlinCoroutines}")

                implementation(Deps.Ktor.clientJava)
                implementation(Deps.Ktor.slf4j)
            }
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
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
        macOS{ v("10_15") }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

