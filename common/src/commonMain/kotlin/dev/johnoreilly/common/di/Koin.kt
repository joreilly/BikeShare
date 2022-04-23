package dev.johnoreilly.common.di

import dev.johnoreilly.common.platformModule
import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.repository.NetworkDb
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.realm.Configuration
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(), platformModule())
    }

// called by iOS etc
fun initKoin() = initKoin {}

fun commonModule() = module {
    single { createJson() }
    single { createHttpClient(get(), get()) }

    single<Configuration> { RealmConfiguration.with(schema = setOf(NetworkDb::class)) }
    single { Realm.open(get()) }

    single { CityBikesRepository() }
    single { CityBikesApi(get()) }
}


fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true; useAlternativeNames = false }

fun createHttpClient(httpClientEngine: HttpClientEngine, json: Json) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json)
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}
