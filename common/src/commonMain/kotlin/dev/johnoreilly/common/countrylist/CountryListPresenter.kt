package dev.johnoreilly.common.countrylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.repository.CityBikesRepository
import dev.johnoreilly.common.screens.CountryListScreen
import dev.johnoreilly.common.screens.NetworkListScreen
import dev.johnoreilly.common.viewmodel.Country
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CircuitInject(CountryListScreen::class, AppScope::class)
@Inject
class CountryListPresenter(
    @Assisted private val navigator: Navigator,
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
}
