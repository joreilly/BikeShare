package dev.johnoreilly.common.viewmodel

import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.stateIn
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.johnoreilly.common.di.Singleton
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject @Singleton
open class StationsViewModelShared(cityBikesRepository: CityBikesRepository) : ViewModel() {
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