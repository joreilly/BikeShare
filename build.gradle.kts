buildscript {
    val compose_version by extra("1.2.0")
    val kotlinVersion: String by project
    println(kotlinVersion)

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.0-rc01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${kotlinVersion}")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.8.0-RC-1.0.8")
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

allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
                using(module("androidx.compose.compiler:compiler:${Versions.composeCompiler}"))
            }
        }
    }
}
