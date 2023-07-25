
object Versions {
    const val kotlin = "1.9.0"
    const val kspPlugin = "1.9.0-1.0.11"
    const val kotlinCoroutines = "1.7.1"
    const val ktor = "2.3.2"
    const val kotlinxSerialization = "1.5.1"
    const val koinCore = "3.4.0"
    const val koinAndroid = "3.4.2"
    const val koinAndroidCompose = "3.4.5"

    const val kmpNativeCoroutinesVersion = "1.0.0-ALPHA-13"
    const val kmmViewModel = "1.0.0-ALPHA-9"

    const val compose = "1.4.3"
    const val composeCompiler = "1.5.0-dev-k1.9.0-6a60475e07f"
    const val jbComposeCompiler = "1.4.8-beta"
    const val navCompose = "2.6.0"
    const val composeMaterial3 = "1.1.1"
    const val composeDesktop = "1.4.3"

    const val realm = "1.10.1"

    const val androidxLifecycle = "2.5.1"

    const val slf4j = "2.0.7"
    const val junit = "4.12"
}

object AndroidSdk {
    const val min = 21
    const val compile = 33
    const val target = compile
}

object Deps {
    const val realm = "io.realm.kotlin:library-base:${Versions.realm}"

    const val androidXLifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}}"

    object Compose {
        const val compiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.navCompose}"

        const val material3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"
        const val material3WindowSizeClass = "androidx.compose.material3:material3-window-size-class:${Versions.composeMaterial3}"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koinCore}"
        const val test = "io.insert-koin:koin-test:${Versions.koinCore}"
        const val android = "io.insert-koin:koin-android:${Versions.koinAndroid}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koinAndroidCompose}"
    }

    object Kotlinx {
        const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    }

    object Ktor {
        const val clientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
        const val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val clientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${Versions.ktor}"
        const val json = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"

        const val clientAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
        const val clientJava = "io.ktor:ktor-client-java:${Versions.ktor}"
        const val slf4j = "org.slf4j:slf4j-simple:${Versions.slf4j}"
        const val clientIos = "io.ktor:ktor-client-ios:${Versions.ktor}"
    }
}
