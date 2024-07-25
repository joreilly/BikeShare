package dev.johnoreilly.common

import io.ktor.client.engine.HttpClientEngine


expect val httpClientEngine: HttpClientEngine

expect fun getCountryName(countryCode: String): String