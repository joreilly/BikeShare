buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://androidx.dev/storage/compose-compiler/repository")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.get()}")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${libs.versions.ksp.get()}")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:${libs.versions.kmpNativeCoroutines.get()}")
        classpath("io.realm.kotlin:gradle-plugin:${libs.versions.realm.get()}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }

    configurations.all {
        resolutionStrategy {
            dependencySubstitution {
                substitute(module("org.jetbrains.kotlin:kotlin-stdlib-wasm:1.9.0"))
                    .using(module("org.jetbrains.kotlin:kotlin-stdlib-wasm-js:1.9.20"))
            }

            eachDependency {
                if (requested.module.name.startsWith("kotlin-stdlib")) {
                    useVersion("1.9.20")
                }
            }
        }
    }
}


