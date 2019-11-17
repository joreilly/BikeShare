package com.surrus.common.repository

import com.surrus.common.ApplicationDispatcher
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CityBikesRepository {
    private val cityBikesApi = CityBikesApi()

    suspend fun fetchBikeShareInfo(network: String) : Network {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network
    }


    fun fetchBikeShareInfo(network: String, success: (Network) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            success(fetchBikeShareInfo(network))
        }
    }
}