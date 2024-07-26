package dev.johnoreilly.common

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale


actual fun getCountryName(countryCode: String): String {
    return NSLocale.currentLocale.displayNameForKey(NSLocaleCountryCode, countryCode) ?: countryCode
}