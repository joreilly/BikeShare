package com.surrus.bikeshare

import com.surrus.common.remote.CityBikesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BikeShareTest {

    val api = CityBikesApi()

    @Test
    fun testFetchNetworkList() = runBlocking {
        val result = api.fetchNetworkList()
        for (network in result.networks) {
            //println(network.id + "    " + network.name)
            if (network.networkId.isNullOrEmpty()) {
                println(network)
            }
        }
        println(result)
    }

    @Test
    fun testFetchNetwork() = runBlocking {
        val result = api.fetchBikeShareInfo("galway")
        println(result)
    }

}
