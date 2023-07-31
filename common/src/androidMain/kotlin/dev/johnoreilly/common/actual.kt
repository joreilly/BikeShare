package dev.johnoreilly.common

import io.ktor.client.engine.android.*
import org.koin.dsl.module
import java.util.Locale

actual fun platformModule() = module {
    single { Android.create() }
}

actual fun getCountryName(countryCode: String): String {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}

