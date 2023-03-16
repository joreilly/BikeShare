buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.8.0-1.0.8")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:${Versions.kmpNativeCoroutinesVersion}")
        classpath("io.realm.kotlin:gradle-plugin:${Versions.realm}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        //maven(url = "https://androidx.dev/snapshots/builds/7909927/artifacts/repository")
        maven("https://androidx.dev/storage/compose-compiler/repository")
    }
}

