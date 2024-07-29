package dev.johnoreilly.common.di

import com.slack.circuit.foundation.Circuit
import dev.johnoreilly.common.screens.CountryListPresenter
import dev.johnoreilly.common.screens.CountryListScreen
import dev.johnoreilly.common.screens.NetworkListPresenter
import dev.johnoreilly.common.screens.NetworkListScreen
import dev.johnoreilly.common.screens.StationListPresenter
import dev.johnoreilly.common.screens.StationListScreen
import dev.johnoreilly.common.ui.BikeShareContent
import dev.johnoreilly.common.ui.CountryListUi
import dev.johnoreilly.common.ui.NetworkListUi
import dev.johnoreilly.common.ui.StationListUI
import io.ktor.client.engine.android.Android
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides


@Component
@Singleton
abstract class AndroidApplicationComponent: SharedApplicationComponent {

    abstract val bikeShareContent: BikeShareContent

    @Provides
    fun provideCircuit(): Circuit = Circuit.Builder()
        .addPresenterFactory(CountryListPresenter.Factory(repository))
        .addPresenterFactory(NetworkListPresenter.Factory(repository))
        .addPresenterFactory(StationListPresenter.Factory(repository))
        .addUi<CountryListScreen, CountryListScreen.State> { state, modifier -> CountryListUi(state, modifier) }
        .addUi<NetworkListScreen, NetworkListScreen.State> { state, modifier -> NetworkListUi(state, modifier) }
        .addUi<StationListScreen, StationListScreen.State> { state, modifier -> StationListUI(state, modifier) }
        .build()

    override fun getHttpClientEngine() = Android.create()

    companion object
}