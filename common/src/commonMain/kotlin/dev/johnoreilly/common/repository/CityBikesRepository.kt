package dev.johnoreilly.common.repository

import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.remote.Station
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn


@Inject
@SingleIn(AppScope::class)
class CityBikesRepository(val cityBikesApi: CityBikesApi,val database: AppDatabase) {
    private val mainScope: CoroutineScope = MainScope()

//    val groupedNetworkList: StateFlow<Map<String,List<Network>>>
//        field = MutableStateFlow<Map<String,List<Network>>>(emptyMap())

    private val _groupedNetworkList = MutableStateFlow<Map<String,List<Network>>>(emptyMap())
    val groupedNetworkList: StateFlow<Map<String,List<Network>>> = _groupedNetworkList


    private val _networkList = MutableStateFlow<List<Network>>(emptyList())

    // Set once fetching the network list has failed on every attempt; cleared when a fetch starts
    private val _networkListLoadError = MutableStateFlow<String?>(null)
    val networkListLoadError: StateFlow<String?> = _networkListLoadError

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

    fun refreshNetworkList() {
        mainScope.launch { fetchAndStoreNetworkList() }
    }

    // Retries with a growing delay, as the network list is only fetched at startup and a single
    // failed request (e.g. a slow or rate-limited API) would otherwise leave the list empty
    private suspend fun fetchAndStoreNetworkList() {
        _networkListLoadError.value = null
        repeat(FETCH_ATTEMPTS) { attempt ->
            try {
                val networkList = cityBikesApi.fetchNetworkList().networks

                database.bikeShareDao().insertNetworkList(networkList.map {
                    Network(it.id, it.name, it.location.city,
                        it.location.country, it.location.latitude, it.location.longitude)
                })
                return
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Exception during fetchAndStoreNetworkList (attempt ${attempt + 1}): $e")
                if (attempt < FETCH_ATTEMPTS - 1) delay(FETCH_RETRY_DELAY * (attempt + 1))
            }
        }
        _networkListLoadError.value = "Could not load bike networks"
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
        private const val FETCH_ATTEMPTS = 3
        private const val FETCH_RETRY_DELAY = 3000L
    }
}