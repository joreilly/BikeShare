
object Versions {
    const val kotlin = "1.9.10"
    const val kspPlugin = "1.9.10-1.0.13"
    const val kotlinCoroutines = "1.7.3"
    const val ktor = "2.3.4"
    const val kotlinxSerialization = "1.6.0"
    const val koinCore = "3.4.3"
    const val koinAndroid = "3.4.3"
    const val koinAndroidCompose = "3.4.6"

    const val kmpNativeCoroutinesVersion = "1.0.0-ALPHA-18"
    const val kmmViewModel = "1.0.0-ALPHA-14"

    const val compose = "1.5.0"
    const val composeCompiler = "1.5.3"
    const val jbComposeCompiler = "1.5.2-beta01"
    const val navCompose = "2.7.2"
    const val composeMaterial3 = "1.1.1"
    const val composeDesktop = "1.5.0"
    const val glance = "1.0.0"

    const val realm = "1.10.2"

    const val androidxLifecycle = "2.5.1"

    const val slf4j = "2.0.7"
    const val junit = "4.12"
}

object AndroidSdk {
    const val min = 21
    const val compile = 34
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

        const val glance = "androidx.glance:glance-appwidget:${Versions.glance}"
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
