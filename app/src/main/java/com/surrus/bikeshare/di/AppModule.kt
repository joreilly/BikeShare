package com.surrus.bikeshare.di

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BikeShareViewModel(get(),get()) }

    single { Kermit(LogcatLogger()) }
}
