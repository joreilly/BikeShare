package com.surrus.common.repository

import com.surrus.common.getApplicationFilesDirectoryPath
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.kodein.db.*
import org.kodein.db.impl.inDir
import org.kodein.db.model.orm.Metadata
import org.kodein.db.orm.kotlinx.KotlinxSerializer


@Serializable
data class NetworkList(override val id: String, val networks: List<Network>): Metadata

@ExperimentalCoroutinesApi
class CityBikesRepository: KoinComponent {
    private val cityBikesApi: CityBikesApi by inject()
    private var db: DB
    private val coroutineScope: CoroutineScope = MainScope()

    private val _groupedNetworkList = MutableStateFlow<Map<String,List<Network>>>(emptyMap())
    val groupedNetworkList: StateFlow<Map<String,List<Network>>> = _groupedNetworkList

    private val _networkList = MutableStateFlow<List<Network>>(emptyList())
    val networkList: StateFlow<List<Network>> = _networkList

    init {
        db = DB
            .inDir(getApplicationFilesDirectoryPath())
            .open("bikeshare_db", TypeTable {
                root<NetworkList>()
            }, KotlinxSerializer())

        db.on<NetworkList>().register {
            didPut { networkListData ->
                _networkList.value = networkListData.networks
                _groupedNetworkList.value = networkListData.networks.groupBy { it.location.country }
            }
            didDelete { }
        }

        coroutineScope.launch { 
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
    suspend fun fetchGroupedNetworkList(success: (Map<String, List<Network>>) -> Unit)  {
        coroutineScope.launch {
            groupedNetworkList.collect {
                success(it)
            }
        }
    }

    @Throws(Exception::class)
    suspend fun fetchNetworkList(success: (List<Network>) -> Unit)  {
        coroutineScope.launch {
            networkList.collect {
                success(it)
            }
        }
    }

    fun pollNetworkUpdates(network: String): Flow<List<Station>> = flow {
        while (true) {
            val stations = cityBikesApi.fetchBikeShareInfo(network).network.stations
            emit(stations)
            delay(POLL_INTERVAL)
        }
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}