package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.stateIn
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class NetworksViewModelShared : ViewModel(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    private val countryCode = MutableStateFlow<String?>(viewModelScope, null)

    @NativeCoroutinesState
    val networkList: StateFlow<List<Network>> = combine(countryCode, cityBikesRepository.groupedNetworkList) { countryCode, groupedNetworkList ->
        groupedNetworkList[countryCode]?.sortedBy { it.city }
    }.filterNotNull().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Set country code to show bike networks for
     */
    fun setCountryCode(countryCode: String) {
        this.countryCode.value = countryCode
    }
}