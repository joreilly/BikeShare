package dev.johnoreilly.common

import io.ktor.client.engine.android.*
import java.util.Locale

actual val httpClientEngine = Android.create()

actual fun getCountryName(countryCode: String): String {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}

