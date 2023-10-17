package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.*
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class StationsViewModelShared : KMMViewModel(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    private val network = MutableStateFlow<String?>(null)

    @NativeCoroutinesState
    val stations = network.filterNotNull().flatMapLatest {
        cityBikesRepository.pollNetworkUpdates(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Set bike share network
     */
    fun setNetwork(networkId: String) {
        network.value = networkId
    }
}