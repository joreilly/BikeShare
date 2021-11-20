
object Versions {
    const val kotlinCoroutines = "1.5.1-new-mm-dev2"
    const val ktor = "1.6.2-native-mm-eap-196"
    const val kotlinxSerialization = "1.3.0"
    const val koin = "3.1.3"
    const val lifecycle = "2.2.0-alpha01"

    const val compose = "1.0.5"
    const val navCompose = "2.4.0-beta02"
    const val slf4j = "1.7.30"

    const val kermit = "0.1.8"
    const val kodein_db = "0.9.0-beta"

    const val junit = "4.12"
}

object AndroidSdk {
    const val min = 21
    const val compile = 31
    const val target = compile
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
    const val navigation = "androidx.navigation:navigation-compose:${Versions.navCompose}"
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

object Kodein {
    val db = "org.kodein.db:kodein-db:${Versions.kodein_db}"
    val dbSerializer = "org.kodein.db:kodein-db-serializer-kotlinx:${Versions.kodein_db}"

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
}

