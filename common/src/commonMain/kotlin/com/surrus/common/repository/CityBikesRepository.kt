package com.surrus.common.repository

import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station


class CityBikesRepository  {
    private val cityBikesApi = CityBikesApi()

    @Throws(Exception::class)
    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network.stations
    }

    @Throws(Exception::class)
    suspend fun fetchNetworkList() : Map<String, List<Network>> {
        val result = cityBikesApi.fetchNetworkList()
        return result.networks.groupBy { it.location.country }
    }
}