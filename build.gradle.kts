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
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${Versions.kspPlugin}")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:${Versions.kmpNativeCoroutinesVersion}")
        classpath("io.realm.kotlin:gradle-plugin:${Versions.realm}")
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
                    .using(module("org.jetbrains.kotlin:kotlin-stdlib-wasm-js:1.9.20-RC"))
            }

            eachDependency {
                if (requested.module.name.startsWith("kotlin-stdlib")) {
                    useVersion("1.9.20-RC")
                }
            }
        }
    }
}


