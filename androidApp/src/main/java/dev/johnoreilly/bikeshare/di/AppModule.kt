package dev.johnoreilly.bikeshare.di

import dev.johnoreilly.bikeshare.ui.viewmodel.BikeShareViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BikeShareViewModel(get()) }
}
