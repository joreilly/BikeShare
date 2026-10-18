package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
open class NetworksViewModelShared(cityBikesRepository: CityBikesRepository) : ViewModel() {
    private val countryCode = MutableStateFlow<String?>(null)

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