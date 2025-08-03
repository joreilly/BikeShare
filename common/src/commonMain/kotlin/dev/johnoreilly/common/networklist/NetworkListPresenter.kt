package dev.johnoreilly.common.networklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.screens.NetworkListScreen
import dev.johnoreilly.common.screens.StationListScreen
import dev.johnoreilly.common.stationlist.StationListPresenter
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CircuitInject(NetworkListScreen::class, AppScope::class)
@Inject
class NetworkListPresenter(
    @Assisted private val screen: NetworkListScreen,
    @Assisted private val navigator: Navigator,
    private val cityBikesRepository: CityBikesRepository,
) : Presenter<NetworkListScreen.State> {

    @Composable
    override fun present(): NetworkListScreen.State {
        val countryName = remember(screen) { getCountryName(screen.countryCode) }
        val groupedNetworkList by cityBikesRepository.groupedNetworkList.collectAsState()
        val networkList by remember {
            derivedStateOf { groupedNetworkList[screen.countryCode]?.sortedBy { it.city } ?: emptyList() }
        }

        var selectedNetworkId by rememberRetained { mutableStateOf<String?>(null) }

        val stationListPresenter = remember(selectedNetworkId) {
            selectedNetworkId?.let { networkId ->
                StationListPresenter(StationListScreen(networkId), navigator, cityBikesRepository)
            }
        }

        val stationListState = stationListPresenter?.present()

        return NetworkListScreen.State(
            countryCode = screen.countryCode, 
            countryName = countryName, 
            networkList = networkList,
            selectedNetworkId = selectedNetworkId,
            stationListState = stationListState,
        ) { event ->
            when (event) {
                is NetworkListScreen.Event.NetworkClicked -> navigator.goTo(StationListScreen(event.networkId))
                is NetworkListScreen.Event.SelectNetwork -> selectedNetworkId = event.networkId
                NetworkListScreen.Event.BackClicked -> navigator.pop()
            }
        }
    }
}
