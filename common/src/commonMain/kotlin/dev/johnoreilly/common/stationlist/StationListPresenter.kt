package dev.johnoreilly.common.stationlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.screens.StationListScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CircuitInject(StationListScreen::class, AppScope::class)
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

