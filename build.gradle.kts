buildscript {
    val kotlinVersion: String by project
    println(kotlinVersion)

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${kotlinVersion}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        }
        maven(url = "https://androidx.dev/snapshots/builds/7909927/artifacts/repository")
    }
}

