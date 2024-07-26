package dev.johnoreilly.common.di

import dev.johnoreilly.common.ui.CountryListScreen
import dev.johnoreilly.common.ui.NetworkListScreen
import dev.johnoreilly.common.ui.StationsScreen
import io.ktor.client.engine.android.Android
import me.tatarka.inject.annotations.Component




@Component
@Singleton
abstract class AndroidApplicationComponent: SharedApplicationComponent {

    abstract val countryListScreen: CountryListScreen
    abstract val networkListScreen: NetworkListScreen
    abstract val stationsScreen: StationsScreen

    override fun getHttpClientEngine() = Android.create()

    companion object
}