plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.github.plnice.canidropjetifier") version "0.5"
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
        }
    }
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

kotlin {
    targets {
        val sdkName: String? = System.getenv("SDK_NAME")

        val isiOSDevice = sdkName.orEmpty().startsWith("iphoneos")
        if (isiOSDevice) {
            iosArm64("iOS64")
        } else {
            iosX64("iOS")
        }

        macosX64("macOS")
        android()
    }


    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "BikeShare common module"
        homepage = "homepage placeholder"
    }



    js {
        browser {
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")

                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json:${Versions.ktor}")
                //implementation("io.ktor:ktor-client-logging:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization:${Versions.ktor}")
                implementation("io.ktor:ktor-client-cio:${Versions.ktor}")


                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}")
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }

        val iOSMain by getting {
            dependencies {
            }
        }

        val macOSMain by getting {
            dependencies {
            }
        }


        val jsMain by getting {
            dependencies {
            }
        }
    }
}
