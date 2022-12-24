package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.*
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class CountriesViewModelShared : KMMViewModel(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    @NativeCoroutinesState
    val countryList: StateFlow<List<String>> = cityBikesRepository.groupedNetworkList.map {
        it.keys.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}