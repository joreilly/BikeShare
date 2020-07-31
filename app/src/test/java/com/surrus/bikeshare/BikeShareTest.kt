package com.surrus.bikeshare

import com.surrus.common.remote.CityBikesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

class BikeShareTest {

    val api = CityBikesApi()

    @Test
    fun testFetchNetworkList() = runBlocking {
        val result = api.fetchNetworkList()
        for (network in result.networks) {
            println(network.networkId + "    " + network.name)
        }
        println(result)
    }

    @Test
    fun testFetchNetwork() = runBlocking {
        val result = api.fetchBikeShareInfo("galway")
        println(result)
    }

}
