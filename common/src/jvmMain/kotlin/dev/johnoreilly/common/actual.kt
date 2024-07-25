package dev.johnoreilly.common

import io.ktor.client.engine.java.*
import java.util.Locale

actual val httpClientEngine = Java.create()

actual fun getCountryName(countryCode: String): String {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}