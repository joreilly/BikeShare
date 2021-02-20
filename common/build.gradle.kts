plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
    }
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

// workaround for https://youtrack.jetbrains.com/issue/KT-43944
android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}


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
        jvm()
    }


    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "BikeShare common module"
        homepage = "homepage placeholder"
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")

                implementation(Ktor.clientCore)
                implementation(Ktor.clientJson)
                implementation(Ktor.clientLogging)
                implementation(Ktor.clientSerialization)

                // Serialize
                implementation(Serialization.core)

                // Kodein-DB
                api("org.kodein.db:kodein-db:${Versions.kodein_db}")
                api("org.kodein.db:kodein-db-serializer-kotlinx:${Versions.kodein_db}")

                // kermit
                api(Deps.kermit)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Ktor.clientAndroid)
            }
        }

        val iOSMain by getting {
            dependencies {
                implementation(Ktor.clientIos)
            }
        }

        val macOSMain by getting {
            dependencies {
                implementation(Ktor.clientIos)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Ktor.clientApache)
                implementation(Ktor.slf4j)
            }
        }
    }
}


multiplatformSwiftPackage {
    packageName("BikeShare")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}
