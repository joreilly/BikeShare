package dev.johnoreilly.common

import io.ktor.client.engine.java.*
import org.koin.dsl.module
import java.util.Locale


actual fun platformModule() = module {
    single { Java.create() }
}

actual fun getCountryName(countryCode: String): String {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}