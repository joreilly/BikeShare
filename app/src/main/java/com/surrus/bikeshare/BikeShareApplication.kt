package com.surrus.bikeshare

import android.app.Application
import com.surrus.bikeshare.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BikeShareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BikeShareApplication)
            modules(appModule)
        }
    }
}