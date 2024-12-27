package dev.johnoreilly.common.screens

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.Station
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