package dev.johnoreilly.common.di

import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.ui.BikeShareApp
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
@SingleIn(AppScope::class)
interface SharedApplicationComponent {
    val bikeShareApp: BikeShareApp

    @Provides
    fun json(): Json = Json { isLenient = true; ignoreUnknownKeys = true; useAlternativeNames = false }

    @Provides
    fun httpClientEngine(): HttpClientEngine

    @Provides
    fun appDatabase(): AppDatabase

    @Provides
    fun httpClient(httpClientEngine: HttpClientEngine, json: Json): HttpClient =
        createHttpClient(httpClientEngine, json)
}

fun createHttpClient(httpClientEngine: HttpClientEngine, json: Json) = HttpClient(httpClientEngine) {
    expectSuccess = true

    defaultRequest {
        url("https://api.citybik.es/")
    }
    install(ContentNegotiation) {
        json(json)
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}
