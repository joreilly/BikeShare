package com.surrus.common.remote

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


@Serializable
data class NetworkResult(val network: Network)


@Serializable
data class Network(val id: String, val name: String, val stations: List<Station>)

@Serializable
data class Station(val id: String,
                   val name: String,
                   @SerialName("empty_slots") val emptySlots: Int,
                   @SerialName("free_bikes") val freeBikes: Int
                   )


class CityBikesApi {
    private val baseUrl = "https://api.citybik.es/v2/networks"

    private val client by lazy {
        HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json(JsonConfiguration(strictMode = false)))
            }
        }
    }

    suspend fun fetchBikeShareInfo(network: String): NetworkResult {
        return client.get("$baseUrl/$network")
    }
}