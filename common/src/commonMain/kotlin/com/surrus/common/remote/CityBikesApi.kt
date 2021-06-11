package com.surrus.common.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.Serializable


@Serializable
data class NetworkResult(val network: Network)

@Serializable
data class NetworkListResult(val networks: List<Network>)

@Serializable
data class Network(val id: String, val name: String, val location: Location, val stations: List<Station> = emptyList())

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

class CityBikesApi(private val client: HttpClient,
    private val baseUrl: String = "https://api.citybik.es/v2/networks"
) {

    suspend fun fetchNetworkList(): NetworkListResult {
        return client.get(baseUrl)
    }

    suspend fun fetchBikeShareInfo(network: String): NetworkResult {
        return client.get("$baseUrl/$network")
    }
}