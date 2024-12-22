package dev.johnoreilly.common.repository

import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.remote.Station
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject


@Inject
class CityBikesRepository(val cityBikesApi: CityBikesApi,val database: AppDatabase) {
    private val mainScope: CoroutineScope = MainScope()

//    val groupedNetworkList: StateFlow<Map<String,List<Network>>>
//        field = MutableStateFlow<Map<String,List<Network>>>(emptyMap())

    private val _groupedNetworkList = MutableStateFlow<Map<String,List<Network>>>(emptyMap())
    val groupedNetworkList: StateFlow<Map<String,List<Network>>> = _groupedNetworkList


    private val _networkList = MutableStateFlow<List<Network>>(emptyList())

    init {
        mainScope.launch {
            launch {
                database.bikeShareDao().getNetworkListAsFlow()
                    .collect {
                        _networkList.value = it.map {
                            Network(it.id, it.name, it.city, it.country, it.latitude, it.longitude)
                        }
                        _groupedNetworkList.value =
                            _networkList.value.groupBy { it.country }
                    }
            }

            fetchAndStoreNetworkList()
        }
    }

    private suspend fun fetchAndStoreNetworkList() {
        try {
            val networkList = cityBikesApi.fetchNetworkList().networks
            println(networkList)

            database.bikeShareDao().insertNetworkList(networkList.map {
                Network(it.id, it.name, it.location.city,
                    it.location.country, it.location.latitude, it.location.longitude)
            })
        } catch (e: Exception) {
            // TODO report error up to UI
            println("Exception during fetchAndStorePeople: $e")
        }
    }


    fun pollNetworkUpdates(network: String): Flow<List<Station>> = flow {
        while (true) {
            try {
                println("pollNetworkUpdates, network = $network")
                val stations = fetchBikeShareInfo(network)
                emit(stations)
            } catch (e: Exception) {
                println("Exception during pollNetworkUpdates: $e")
            }
            delay(POLL_INTERVAL)
        }
    }

    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network.stations
    }

    companion object {
        private const val POLL_INTERVAL = 30000L
    }
}