package dev.johnoreilly.common.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dev.johnoreilly.common.screens.CountryListUiFactory
import dev.johnoreilly.common.screens.NetworkListUiFactory
import dev.johnoreilly.common.screens.StationListUiFactory
import dev.johnoreilly.common.countrylist.CountryListPresenterFactory
import dev.johnoreilly.common.networklist.NetworkListPresenterFactory
import dev.johnoreilly.common.stationlist.StationListPresenterFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface SharedUiComponent {
    @IntoSet
    @Provides
    fun bindCountryListPresenterFactory(factory: CountryListPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    fun bindCountryListUiFactory(factory: CountryListUiFactory): Ui.Factory = factory

    @IntoSet
    @Provides
    fun bindNetworkListPresenterFactory(factory: NetworkListPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    fun bindNetworkListUiFactory(factory: NetworkListUiFactory): Ui.Factory = factory

    @IntoSet
    @Provides
    fun bindStationListPresenterFactory(factory: StationListPresenterFactory): Presenter.Factory = factory

    @IntoSet
    @Provides
    fun bindStationListUiFactory(factory: StationListUiFactory): Ui.Factory = factory

    @Provides
    fun provideCircuit(
        uiFactories: Set<Ui.Factory>,
        presenterFactories: Set<Presenter.Factory>
    ): Circuit = Circuit.Builder()
        .addUiFactories(uiFactories)
        .addPresenterFactories(presenterFactories)
        .build()

}