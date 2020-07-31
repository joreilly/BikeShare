package com.surrus.common.repository

import com.surrus.common.ktorScope
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network


class CityBikesRepository  {
    private val cityBikesApi = CityBikesApi()

    suspend fun fetchBikeShareInfo(network: String) : Network {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network
    }

    suspend fun fetchNetworkList() : Map<String, List<Network>> {
        val result = cityBikesApi.fetchNetworkList()
        return result.networks.groupBy { it.location.country }
    }


    fun fetchNetworkList(success: (Map<String, List<Network>>) -> Unit) {
        ktorScope {
            success(fetchNetworkList())
        }
    }

    fun fetchBikeShareInfo(network: String, success: (Network) -> Unit) {
        ktorScope {
            success(fetchBikeShareInfo(network))
        }
    }
}