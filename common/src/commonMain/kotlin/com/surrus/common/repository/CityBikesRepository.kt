package com.surrus.common.repository

import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station


class CityBikesRepository  {
    private val cityBikesApi = CityBikesApi()

    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        try {
            val result = cityBikesApi.fetchBikeShareInfo(network)
            return result.network.stations
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun fetchNetworkList() : Map<String, List<Network>> {
        try {
            val result = cityBikesApi.fetchNetworkList()
            return result.networks.groupBy { it.location.country }
        } catch (e: Exception) {
            return emptyMap()
        }
    }
}