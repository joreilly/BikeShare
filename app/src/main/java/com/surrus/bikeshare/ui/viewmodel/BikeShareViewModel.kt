package com.surrus.bikeshare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station
import com.surrus.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

data class Country(val code: String, val displayName: String)

class BikeShareViewModel(private val cityBikesRepository: CityBikesRepository) : ViewModel() {
    val stations = MutableStateFlow<List<Station>>(emptyList())
    val groupedNetworks = MutableStateFlow<Map<Country,List<Network>>>(emptyMap())

    private var networkId = ""

    init {
        getGroupedNetworks()
    }

    private fun getStations() {
        viewModelScope.launch {
            val result = cityBikesRepository.fetchBikeShareInfo(networkId)
            stations.value = result
        }
    }

    private fun getGroupedNetworks() {
        viewModelScope.launch {
            val networks = cityBikesRepository.fetchNetworkList()

            val grouped = networks.groupBy { it.location.country }.mapKeys {
                val countryCode = it.key.toLowerCase()
                val locale = Locale("", countryCode)
                val countryName = locale.displayCountry
                Country(countryCode, countryName)
            }

            groupedNetworks.value = grouped
        }
    }


    fun setCity(city: String) {
        this.networkId = city.toLowerCase()
        getStations()
    }
}