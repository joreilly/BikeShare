package com.surrus.common.repository

import com.surrus.common.ktorScope
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network


class CityBikesRepository {
    private val cityBikesApi = CityBikesApi()

    suspend fun fetchBikeShareInfo(network: String) : Network {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network
    }


    fun fetchBikeShareInfo(network: String, success: (Network) -> Unit) {
        ktorScope {
            success(fetchBikeShareInfo(network))
        }
    }
}