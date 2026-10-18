package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
open class StationsViewModelShared(cityBikesRepository: CityBikesRepository) : ViewModel() {
    private val network = MutableStateFlow<String?>(null)

    val stations: StateFlow<List<Station>> = network.filterNotNull().flatMapLatest {
        cityBikesRepository.pollNetworkUpdates(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Set bike share network
     */
    fun setNetwork(networkId: String) {
        network.value = networkId
    }
}