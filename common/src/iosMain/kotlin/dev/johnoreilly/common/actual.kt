package dev.johnoreilly.common

import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleKey
import platform.Foundation.currentLocale


actual fun platformModule() = module {
    single { Darwin.create() }
}


actual fun getCountryName(countryCode: String): String {
    return NSLocale.currentLocale.displayNameForKey(NSLocaleCountryCode, countryCode) ?: countryCode
}