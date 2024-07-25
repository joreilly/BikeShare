package dev.johnoreilly.common

import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale


actual val httpClientEngine = Darwin.create()

actual fun getCountryName(countryCode: String): String {
    return NSLocale.currentLocale.displayNameForKey(NSLocaleCountryCode, countryCode) ?: countryCode
}