package com.surrus.bikeshare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Station
import com.surrus.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

data class Country(val code: String, val displayName: String)

class BikeShareViewModel(private val cityBikesRepository: CityBikesRepository) : ViewModel() {
    private var networkId = ""
    val stations = MutableStateFlow<List<Station>>(emptyList())

    val groupedNetworks = cityBikesRepository.groupedNetworkList.map {
        it.mapKeys {
            val countryCode = it.key.toLowerCase()
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            Country(countryCode, countryName)
        }
    }


    private fun getStations() {
        viewModelScope.launch {
            val result = cityBikesRepository.fetchBikeShareInfo(networkId)
            stations.value = result
        }
    }

    fun setCity(city: String) {
        this.networkId = city.toLowerCase()
        getStations()
    }
}