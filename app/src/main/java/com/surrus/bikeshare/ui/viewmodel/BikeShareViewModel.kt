package com.surrus.bikeshare.ui.viewmodel

import androidx.lifecycle.*
import co.touchlab.kermit.Kermit
import com.surrus.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import java.util.*

data class Country(val code: String, val displayName: String)

class BikeShareViewModel(
    private val cityBikesRepository: CityBikesRepository,
    private val logger: Kermit
) : ViewModel() {

    val network = MutableStateFlow<String>("")
    val stations = network.flatMapLatest { cityBikesRepository.pollNetworkUpdates(it) }

    val groupedNetworks = cityBikesRepository.groupedNetworkList.map {
        it.mapKeys {
            val countryCode = it.key.lowercase(Locale.getDefault())
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            Country(countryCode, countryName)
        }
    }

    fun setCity(city: String) {
        network.value = city
    }
}