package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.stateIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class StationsViewModelShared : ViewModel(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    private val network = MutableStateFlow<String?>(viewModelScope, null)

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