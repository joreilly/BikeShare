package com.surrus.bikeshare.di

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.repository.CityBikesRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.logging.Logger.getLogger

val appModule = module {
    viewModel { BikeShareViewModel(get(),get()) }

    single { CityBikesRepository() }
    single { Kermit(LogcatLogger()) }
}
