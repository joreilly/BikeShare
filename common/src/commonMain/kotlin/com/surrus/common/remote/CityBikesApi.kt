package com.surrus.common.remote

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json



@Serializable
data class NetworkResult(val network: Network)

@Serializable
data class NetworkListResult(val networks: List<Network>)


@Serializable
data class Network(@SerialName("id") val networkId: String, val name: String, val location: Location, val stations: List<Station> = emptyList())


@Serializable
data class Location(val city: String, val country: String, val latitude: Double, val longitude: Double)


@Serializable
data class Station(val id: String? = "", val name: String,
                   val empty_slots: Int? = 0, val free_bikes: Int? = 0,
                   val latitude: Double, val longitude: Double) {}

fun Station.freeBikes(): Int {
    return free_bikes?: 0
}
fun Station.slots(): Int {
    return (empty_slots ?: 0) + (free_bikes ?: 0)
}

class CityBikesApi {
    private val baseUrl = "https://api.citybik.es/v2/networks"

    private val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }

    private val client by lazy {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(nonStrictJson)
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
        }
    }

    suspend fun fetchNetworkList(): NetworkListResult {
        return client.get("$baseUrl")
    }


    suspend fun fetchBikeShareInfo(network: String): NetworkResult {
        return client.get("$baseUrl/$network")
    }
}