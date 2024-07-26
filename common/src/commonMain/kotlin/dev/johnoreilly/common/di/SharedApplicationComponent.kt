package dev.johnoreilly.common.di

import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.repository.NetworkDb
import dev.johnoreilly.common.viewmodel.CountriesViewModelShared
import dev.johnoreilly.common.viewmodel.NetworksViewModelShared
import dev.johnoreilly.common.viewmodel.StationsViewModelShared
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class Singleton

interface SharedApplicationComponent {

    val countriesViewModel: CountriesViewModelShared
    val networksViewModel: NetworksViewModelShared
    val stationsViewModel: StationsViewModelShared


    val repository: CityBikesRepository
    val cityBikesApi: CityBikesApi

    val json: Json
        @Provides get() = Json { isLenient = true; ignoreUnknownKeys = true; useAlternativeNames = false }

    val realm: Realm
        @Provides get() {
            val config = RealmConfiguration.create(schema = setOf(NetworkDb::class))
            return Realm.open(config)
        }

    @Provides
    fun getHttpClientEngine(): HttpClientEngine

    @Provides
    fun httpClient(): HttpClient = createHttpClient(getHttpClientEngine(), json)
}

fun createHttpClient(httpClientEngine: HttpClientEngine, json: Json) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json)
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}
