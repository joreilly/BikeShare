package dev.johnoreilly.common.networklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.johnoreilly.common.screens.NetworkListScreen
import dev.johnoreilly.common.screens.StationListScreen
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.repository.CityBikesRepository
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
class NetworkListPresenterFactory(
    private val presenterFactory: (NetworkListScreen, Navigator) -> NetworkListPresenter
) : Presenter.Factory {
    override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
        return when (screen) {
            is NetworkListScreen -> presenterFactory(screen, navigator)
            else -> null
        }
    }
}

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
