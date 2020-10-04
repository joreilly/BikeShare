package com.surrus.common.repository

import com.surrus.common.getApplicationFilesDirectoryPath
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.db.*
import org.kodein.db.impl.factory
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import org.kodein.memory.use


class CityBikesRepository  {
    private val cityBikesApi = CityBikesApi()

    private lateinit var db: DB

    init {
        db = DB.factory
            .inDir(getApplicationFilesDirectoryPath())
            .open("bikeshare_db", TypeTable {
                root<Network>()
            }, KotlinxSerializer())

        GlobalScope.launch(Dispatchers.Main) {
            fetchAndStoreNetworkList()
        }
    }

    private suspend fun fetchAndStoreNetworkList() {
        val networkList = cityBikesApi.fetchNetworkList().networks
        networkList.forEach {
            db.put(it)
        }
    }

    @Throws(Exception::class)
    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network.stations
    }

    @Throws(Exception::class)
    suspend fun fetchGroupedNetworkList() : Map<String, List<Network>> {
        val result = cityBikesApi.fetchNetworkList()
        return result.networks.groupBy { it.location.country }
    }

    @Throws(Exception::class)
    suspend fun fetchNetworkList() : List<Network> {
        return db.find<Network>().all().useModels { it.toList() }
    }

}