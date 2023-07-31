package dev.johnoreilly.common

import org.koin.core.module.Module

expect fun platformModule(): Module

expect fun getCountryName(countryCode: String): String