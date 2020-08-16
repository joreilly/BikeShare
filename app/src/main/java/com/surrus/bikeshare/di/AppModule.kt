package com.surrus.bikeshare.di

import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.repository.CityBikesRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BikeShareViewModel(get()) }

    single { CityBikesRepository() }
}
