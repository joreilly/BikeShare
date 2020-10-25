package com.surrus.common.repository

import com.surrus.common.getApplicationFilesDirectoryPath
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.kodein.db.*
import org.kodein.db.impl.factory
import org.kodein.db.model.orm.Metadata
import org.kodein.db.orm.kotlinx.KotlinxSerializer


@Serializable
data class NetworkList(override val id: String, val networks: List<Network>) : Metadata

@ExperimentalCoroutinesApi
class CityBikesRepository  {
    private val cityBikesApi = CityBikesApi()
    private var db: DB

    val groupedNetworkList = MutableStateFlow<Map<String,List<Network>>>(emptyMap())

    init {
        db = DB.factory
            .inDir(getApplicationFilesDirectoryPath())
            .open("bikeshare_db", TypeTable {
                root<NetworkList>()
            }, KotlinxSerializer())

        db.on<NetworkList>().register {
            didPut { networkListData ->
                groupedNetworkList.value = networkListData.networks.groupBy { it.location.country }
            }
            didDelete { }
        }

        GlobalScope.launch(Dispatchers.Main) {
            fetchAndStoreNetworkList()
        }
    }

    private suspend fun fetchAndStoreNetworkList() {
        val networkList = cityBikesApi.fetchNetworkList().networks
        val networkListData = NetworkList("networkList", networkList)
        db.put(networkListData)
    }

    @Throws(Exception::class)
    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network.stations
    }

    @Throws(Exception::class)
    suspend fun fetchGroupedNetworkList() : Map<String, List<Network>> {
        return groupedNetworkList.value
    }

    @Throws(Exception::class)
    suspend fun fetchNetworkList() : List<Network> {
        val key = db.key<NetworkList>("networkList")
        return db[key]?.networks ?: emptyList()
    }
}