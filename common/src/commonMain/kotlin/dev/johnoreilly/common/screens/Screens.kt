package dev.johnoreilly.common.screens

import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.countrylist.CountryListUi
import dev.johnoreilly.common.networklist.NetworkListUi
import dev.johnoreilly.common.stationlist.StationListUI
import dev.johnoreilly.common.viewmodel.Country
import me.tatarka.inject.annotations.Inject


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



// TODO move these somewhere else

@Inject
class CountryListUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is CountryListScreen -> {
            ui<CountryListScreen.State> { state, modifier ->
                CountryListUi(state, modifier)
            }
        }
        else -> null
    }
}


@Inject
class NetworkListUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is NetworkListScreen -> {
            ui<NetworkListScreen.State> { state, modifier ->
                NetworkListUi(state, modifier)
            }
        }
        else -> null
    }
}

@Inject
class StationListUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is StationListScreen -> {
            ui<StationListScreen.State> { state, modifier ->
                StationListUI(state, modifier)
            }
        }
        else -> null
    }
}


