package com.surrus.bikeshare.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Station
import com.surrus.common.repository.CityBikesRepository
import kotlinx.coroutines.launch

class BikeShareViewModel(private val cityBikesRepository: CityBikesRepository) : ViewModel() {
    val stations = MutableLiveData<List<Station>>()

    private var city: String = "galway"

    init {
        getStations()
    }

    private fun getStations() {
        viewModelScope.launch {
            val result = cityBikesRepository.fetchBikeShareInfo(city)
            stations.postValue(result)
            Log.d("BikeShare", "got results")
        }
    }


    fun setCity(city: String) {
        this.city = city.toLowerCase()
        getStations()
    }
}