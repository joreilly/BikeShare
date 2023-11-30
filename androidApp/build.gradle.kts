@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.*


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}


val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
try {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
catch(e: Exception) {
}

val versionMajor = 1
val versionMinor = 0

val versionNum: String? by project

fun versionCode(): Int {
    versionNum?.let {
        val code: Int = (versionMajor * 1000000) + (versionMinor * 1000) + it.toInt()
        println("versionCode is set to $code")
        return code
    } ?: return 1
}

fun versionName(): String {
    versionNum?.let {
        val name = "${versionMajor}.${versionMinor}.${versionNum}"
        println("versionName is set to $name")
        return name
    } ?: return "1.0"
}


android {
    namespace = "dev.johnoreilly.bikeshare"
    compileSdk = libs.versions.compileSdk.get().toInt()

    signingConfigs {

        create("release") {
            (keystoreProperties["keyPath"] as String?)?.let {
                storeFile = file(it)
            }
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storePassword = keystoreProperties["storePassword"] as String?
            enableV2Signing = true
        }
    }


    defaultConfig {
        applicationId = "dev.johnoreilly.bikeshare"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        this.versionCode = versionCode()
        this.versionName = versionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // see https://api.citybik.es/v2/networks/ for possible network values
        buildConfigField("String", "BIKE_NETWORK", "\"\"")
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

//    kotlinOptions {
//        freeCompilerArgs += listOf(
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
//        )
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    with (Deps.Compose) {
        implementation(compiler)
        implementation(ui)
        implementation(uiGraphics)
        implementation(uiTooling)
        implementation(foundationLayout)
        implementation(navigation)
        implementation(glance)

        implementation(material3)
        implementation(material3WindowSizeClass)
    }


    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)


    //api(libs.kmmViewModel)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")

    implementation(project(":common"))

}