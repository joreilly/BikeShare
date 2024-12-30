package dev.johnoreilly.common.networklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.screens.NetworkListScreen
import dev.johnoreilly.common.screens.StationListScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CircuitInject(NetworkListScreen::class, AppScope::class)
@Inject
class NetworkListPresenter(
    @Assisted private val screen: NetworkListScreen,
    @Assisted private val navigator: Navigator,
    private val cityBikesRepository: CityBikesRepository
) : Presenter<NetworkListScreen.State> {
    @Composable
    override fun present(): NetworkListScreen.State {
        val groupedNetworkList by cityBikesRepository.groupedNetworkList.collectAsState()
        val networkList = groupedNetworkList[screen.countryCode]?.sortedBy { it.city } ?: emptyList()
        val oountryName = getCountryName(screen.countryCode)
        return NetworkListScreen.State(screen.countryCode, oountryName, networkList) { event ->
            when (event) {
                is NetworkListScreen.Event.NetworkClicked -> navigator.goTo(StationListScreen(event.networkId))
                NetworkListScreen.Event.BackClicked -> navigator.pop()
            }
        }
    }
}
