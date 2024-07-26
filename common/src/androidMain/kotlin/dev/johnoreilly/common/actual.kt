package dev.johnoreilly.common

import java.util.Locale

actual fun getCountryName(countryCode: String): String {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}

