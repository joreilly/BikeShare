package dev.johnoreilly.bikeshare.di

import dev.johnoreilly.common.viewmodel.CountriesViewModelShared
import dev.johnoreilly.common.viewmodel.NetworksViewModelShared
import dev.johnoreilly.common.viewmodel.StationsViewModelShared
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CountriesViewModelShared() }
    viewModel { NetworksViewModelShared() }
    viewModel { StationsViewModelShared() }
}
