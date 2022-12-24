package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.*
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class NetworksViewModelShared : KMMViewModel(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    private val countryCode = MutableStateFlow<String?>(null)

    @NativeCoroutinesState
    val networkList: StateFlow<List<Network>> = combine(countryCode, cityBikesRepository.groupedNetworkList) { countryCode, groupedNetworkList ->
        groupedNetworkList[countryCode]?.sortedBy { it.city }
    }.filterNotNull().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setCountryCode(countryCode: String) {
        this.countryCode.value = countryCode
    }
}