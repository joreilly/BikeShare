package dev.johnoreilly.common.stationlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.johnoreilly.common.screens.StationListScreen
import dev.johnoreilly.common.repository.CityBikesRepository
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class StationListPresenterFactory(
    private val presenterFactory: (StationListScreen, Navigator) -> StationListPresenter
) : Presenter.Factory {
    override fun create(screen: Screen, navigator: Navigator, context: CircuitContext): Presenter<*>? {
        return when (screen) {
            is StationListScreen -> presenterFactory(screen, navigator)
            else -> null
        }
    }
}

@Inject
class StationListPresenter(
    @Assisted private val screen: StationListScreen,
    @Assisted private val navigator: Navigator,
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
}

