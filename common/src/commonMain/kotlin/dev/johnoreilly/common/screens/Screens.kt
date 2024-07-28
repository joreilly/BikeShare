package dev.johnoreilly.common.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.viewmodel.Country


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Parcelize


@Parcelize
data object CountryListScreen : Screen {
    data class State(
        val countryList: List<Country>,
        val eventSink: (Event) -> Unit
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class CountryClicked(val countryCode: String) : Event()
    }
}

@Parcelize
data class NetworkListScreen(val countryCode: String) : Screen {
    data class State(
        val countryCode: String,
        val countryName: String,
        val networkList: List<Network>,
        val eventSink: (Event) -> Unit
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class NetworkClicked(val networkId: String) : Event()
        data object BackClicked : Event()
    }
}

@Parcelize
data class StationListScreen(val networkId: String) : Screen {
    data class State(
        val networkId: String,
        val stationList: List<Station>,
        val eventSink: (Event) -> Unit
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object BackClicked : Event()
    }
}


// Presenters (TODO: where should we put these?)

class CountryListPresenter(
    private val navigator: Navigator,
    private val cityBikesRepository: CityBikesRepository
) : Presenter<CountryListScreen.State> {
    @Composable
    override fun present(): CountryListScreen.State {
        val groupedNetworkList by cityBikesRepository.groupedNetworkList.collectAsState()
        val countryCodeList = groupedNetworkList.keys.toList()
        val countryList = countryCodeList.map { countryCode -> Country(countryCode, getCountryName(countryCode)) }
            .sortedBy { it.displayName }
        return CountryListScreen.State(countryList) { event ->
            when (event) {
                is CountryListScreen.Event.CountryClicked -> navigator.goTo(NetworkListScreen(event.countryCode))
            }
        }
    }

    class Factory(private val repository: CityBikesRepository) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                CountryListScreen -> return CountryListPresenter(navigator, repository)
                else -> null
            }
        }
    }
}


class NetworkListPresenter(
    private val screen: NetworkListScreen,
    private val navigator: Navigator,
    private val cityBikesRepository: CityBikesRepository
) : Presenter<NetworkListScreen.State> {
    @Composable
    override fun present(): NetworkListScreen.State {
        val groupedNetworkList by cityBikesRepository.groupedNetworkList.collectAsState()
        val countryList = groupedNetworkList[screen.countryCode]?.sortedBy { it.city } ?: emptyList()
        val oountryName = getCountryName(screen.countryCode)
        return NetworkListScreen.State(screen.countryCode, oountryName, countryList) { event ->
            when (event) {
                is NetworkListScreen.Event.NetworkClicked -> navigator.goTo(StationListScreen(event.networkId))
                NetworkListScreen.Event.BackClicked -> navigator.pop()
            }
        }
    }

    class Factory(private val repository: CityBikesRepository) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is NetworkListScreen -> return NetworkListPresenter(screen, navigator, repository)
                else -> null
            }
        }
    }
}


class StationListPresenter(
    private val screen: StationListScreen,
    private val navigator: Navigator,
    private val cityBikesRepository: CityBikesRepository
) : Presenter<StationListScreen.State> {
    @Composable
    override fun present(): StationListScreen.State {
        val stationList by cityBikesRepository.pollNetworkUpdates(screen.networkId).collectAsState(emptyList())
        return StationListScreen.State(screen.networkId, stationList) { event ->
            when (event) {
                StationListScreen.Event.BackClicked -> navigator.pop()
            }
        }
    }

    class Factory(private val repository: CityBikesRepository) : Presenter.Factory {
        override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
            return when (screen) {
                is StationListScreen -> return StationListPresenter(screen, navigator, repository)
                else -> null
            }
        }
    }
}

