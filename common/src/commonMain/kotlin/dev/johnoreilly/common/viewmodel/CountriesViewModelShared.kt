package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.*
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class Country(val code: String, val displayName: String)

open class CountriesViewModelShared : KMMViewModel(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    /**
     * List of countries for which there are bike networks
     */
    @NativeCoroutinesState
    val countryList: StateFlow<List<Country>> = cityBikesRepository.groupedNetworkList.map {
        it.keys.toList().map { countryCode ->
            Country(countryCode, getCountryName(countryCode))
        }.sortedBy { it.displayName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}