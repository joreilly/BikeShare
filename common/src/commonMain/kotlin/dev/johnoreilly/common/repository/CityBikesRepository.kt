package dev.johnoreilly.common.repository

import dev.johnoreilly.common.di.Singleton
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.remote.Station
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject



class NetworkDb: RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var city: String = ""
    var country: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}


@Inject @Singleton
class CityBikesRepository(val cityBikesApi: CityBikesApi,val  realm: Realm) {
    private val mainScope: CoroutineScope = MainScope()

//    val groupedNetworkList: StateFlow<Map<String,List<Network>>>
//        field = MutableStateFlow<Map<String,List<Network>>>(emptyMap())

    private val _groupedNetworkList = MutableStateFlow<Map<String,List<Network>>>(emptyMap())
    val groupedNetworkList: StateFlow<Map<String,List<Network>>> = _groupedNetworkList


    private val _networkList = MutableStateFlow<List<Network>>(emptyList())

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
        try {
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
                    }, updatePolicy = UpdatePolicy.ALL)
                }
            }
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