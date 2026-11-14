package dev.johnoreilly.common.repository

import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.remote.Station
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
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

    // The stored network list, fetching it first if it hasn't been stored yet (e.g. when read
    // before the app's startup fetch has finished). Throws if it can't be fetched.
    suspend fun getNetworkList(): List<Network> {
        val stored = database.bikeShareDao().getNetworkListAsFlow().first()
        if (stored.isNotEmpty()) return stored
        return cityBikesApi.fetchNetworkList().networks.map {
            Network(it.id, it.name, it.location.city,
                it.location.country, it.location.latitude, it.location.longitude)
        }
    }

    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network.stations
    }

    // Like fetchBikeShareInfo, but null when there's no network with that ID (the API answers
    // 404) rather than throwing, so callers can tell an unknown network from a failed request.
    suspend fun fetchBikeShareInfoOrNull(network: String): List<Station>? =
        try {
            fetchBikeShareInfo(network)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) null else throw e
        }

    companion object {
        private const val POLL_INTERVAL = 30000L
        private const val FETCH_ATTEMPTS = 3
        private const val FETCH_RETRY_DELAY = 3000L
    }
}