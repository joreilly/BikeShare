package com.surrus.bikeshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station
import com.surrus.common.repository.CityBikesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class BikeShareViewModel(private val cityBikesRepository: CityBikesRepository) : ViewModel() {
    val stations = MutableStateFlow<List<Station>>(emptyList())
    val networks = MutableStateFlow<List<Network>>(emptyList())

    private var city: String = "galway"

    init {
        getStations()
        getNetworks()
    }

    private fun getStations() {
        viewModelScope.launch {
            val result = cityBikesRepository.fetchBikeShareInfo(city)
            stations.value = result
        }
    }

    private fun getNetworks() {
        viewModelScope.launch {
            val result = cityBikesRepository.fetchNetworkList()
            networks.value = result
        }
    }

    fun setCity(city: String) {
        this.city = city.toLowerCase()
        getStations()
    }
}