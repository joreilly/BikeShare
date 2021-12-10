package com.surrus.common.repository

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import com.surrus.common.model.Network
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Station
import io.realm.*
import io.realm.annotations.PrimaryKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject



class NetworkDb: RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var city: String = ""
    var country: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}

@ExperimentalCoroutinesApi
class CityBikesRepository: KoinComponent {
    private val cityBikesApi: CityBikesApi by inject()
    private val realm: Realm by inject()

    @NativeCoroutineScope
    private val mainScope: CoroutineScope = MainScope()

    private val _groupedNetworkList = MutableStateFlow<Map<String,List<Network>>>(emptyMap())
    val groupedNetworkList: StateFlow<Map<String,List<Network>>> = _groupedNetworkList

    private val _networkList = MutableStateFlow<List<Network>>(emptyList())
    val networkList: StateFlow<List<Network>> = _networkList

    init {
        mainScope.launch {
            launch {
                realm.query<NetworkDb>().asFlow()
                    .map { it.list }
                    .collect { it: RealmResults<NetworkDb> ->
                        _networkList.value = it.toList().map {
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
        val networkList = cityBikesApi.fetchNetworkList().networks
        println(networkList)

        realm.write {
            networkList.forEach { networkDto ->
                copyToRealm(NetworkDb().apply {
                    id = networkDto.id
                    name = networkDto.name
                    city = networkDto.location.city
                    country = networkDto.location.country
                    latitude = networkDto.location.latitude
                    longitude = networkDto.location.longitude
                }, updatePolicy = MutableRealm.UpdatePolicy.ALL)
            }
        }
    }


    fun pollNetworkUpdates(network: String): Flow<List<Station>> = flow {
        while (true) {
            println("pollNetworkUpdates, network = $network")
            val stations = fetchBikeShareInfo(network)
            emit(stations)
            delay(POLL_INTERVAL)
        }
    }

    suspend fun fetchBikeShareInfo(network: String) : List<Station> {
        val result = cityBikesApi.fetchBikeShareInfo(network)
        return result.network.stations
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}