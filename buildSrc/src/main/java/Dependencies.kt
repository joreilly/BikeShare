
object Versions {
    const val kotlin = "1.4.31"
    const val kotlinCoroutines = "1.4.3-native-mt"
    const val ktor = "1.5.2"
    const val kotlinxSerialization = "1.0.1"
    const val koin = "3.0.1-beta-1"
    const val ktx = "1.0.1"
    const val lifecycle = "2.2.0-alpha01"
    const val compose = "1.0.0-beta01"
    const val nav_compose = "1.0.0-alpha08"
    const val accompanist = "0.6.0"
    const val slf4j = "1.7.30"

    const val kermit = "0.1.8"
    const val kodein_db = "0.6.0-beta"

    const val junit = "4.12"
}

object Deps {
    const val kermit = "co.touchlab:kermit:${Versions.kermit}"
}

object Compose {
    const val ui = "androidx.compose.ui:ui:${Versions.compose}"
    const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
    const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
    const val material = "androidx.compose.material:material:${Versions.compose}"
    const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:${Versions.compose}"
    const val navigation = "androidx.navigation:navigation-compose:${Versions.nav_compose}"
    const val accompanist= "dev.chrisbanes.accompanist:accompanist-coil:${Versions.accompanist}"
}

object Koin {
    val core = "io.insert-koin:koin-core:${Versions.koin}"
    val test = "io.insert-koin:koin-test:${Versions.koin}"
    val android = "io.insert-koin:koin-android:${Versions.koin}"
    val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
}

object Serialization {
    val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"
}


object Ktor {
    val clientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
    val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
    val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
    val clientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"

    val clientAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
    val clientApache = "io.ktor:ktor-client-apache:${Versions.ktor}"
    val slf4j = "org.slf4j:slf4j-simple:${Versions.slf4j}"
    val clientIos = "io.ktor:ktor-client-ios:${Versions.ktor}"
    val clientCio = "io.ktor:ktor-client-cio:${Versions.ktor}"
    val clientJs = "io.ktor:ktor-client-js:${Versions.ktor}"
}

