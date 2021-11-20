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

        val kmpNativeCoroutinesVersion = if (kotlinVersion == "1.6.0") "0.9.0-new-mm" else "0.8.0"
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:$kmpNativeCoroutinesVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        maven(url = "https://androidx.dev/snapshots/builds/7909927/artifacts/repository")
    }
}

