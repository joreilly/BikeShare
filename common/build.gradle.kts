import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("io.realm.kotlin") version Versions.realm
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.rickclephas.kmp.nativecoroutines")
    //id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
}


// CocoaPods requires the podspec to have a version.
version = "1.0"

kotlin {
    //val xcf = XCFramework()


    //targets {
//        val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget = when {
//            System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
//            System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
//            else -> ::iosX64
//        }
//        iosTarget("iOS") {
//            binaries.framework {
//                baseName = "BikeShareKit"
//                xcf.add(this)
//            }
//        }

        val xcf = XCFramework("BikeShareKit")
        listOf(iosX64(), iosArm64(), iosSimulatorArm64())
            .forEach {
                it.binaries.framework {
                    baseName = "BikeShareKit"
                    isStatic = false
                    xcf.add(this)
                }
            }

        macosArm64("macOS") {
            binaries.framework {
                baseName = "BikeShareKit"
                xcf.add(this)
            }
        }
        android()
        jvm()
    //}


    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "BikeShare common module"
        homepage = "homepage placeholder"
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
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientAndroid)
            }
        }

//        val iOSMain by getting {
//            dependencies {
//                implementation(Deps.Ktor.clientIos)
//            }
//        }
//        val iOSTest by getting {
//        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Deps.Ktor.clientIos)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val macOSMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientIos)
            }
        }

        val jvmMain by getting {
            dependencies {
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

//multiplatformSwiftPackage {
//    packageName("BikeShareKit")
//    swiftToolsVersion("5.3")
//    targetPlatforms {
//        iOS { v("13") }
//        macOS{ v("10_15") }
//    }
//}
