package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

data class Country(val code: String, val displayName: String)

@Inject
open class CountriesViewModelShared(private val cityBikesRepository: CityBikesRepository) : ViewModel() {

    /**
     * List of countries for which there are bike networks
     */
    val countryList: StateFlow<List<Country>> = cityBikesRepository.groupedNetworkList.map {
        it.keys.toList().map { countryCode ->
            Country(countryCode, getCountryName(countryCode))
        }.sortedBy { it.displayName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Set when the network list couldn't be loaded, so an empty list can show why
     */
    val loadError: StateFlow<String?> = cityBikesRepository.networkListLoadError

    fun retry() {
        cityBikesRepository.refreshNetworkList()
    }
}