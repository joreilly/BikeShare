package com.surrus.common.di

import co.touchlab.kermit.Kermit
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.repository.CityBikesRepository
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule())
    }

// called by iOS etc
fun initKoin() = initKoin {}

fun commonModule() = module {
    single { createJson() }
    single { createHttpClient(get()) }
    single { CityBikesRepository() }
    single { CityBikesApi(get()) }
}


fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true; useAlternativeNames = false }

fun createHttpClient(json: Json,) = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}
