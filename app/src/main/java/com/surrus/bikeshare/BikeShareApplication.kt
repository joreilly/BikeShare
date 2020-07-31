package com.surrus.bikeshare

import android.app.Application
import com.surrus.bikeshare.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger

class BikeShareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // commenting out for now until Kotlin 1.4 supported
            //androidLogger()
            EmptyLogger()
            androidContext(this@BikeShareApplication)
            modules(appModule)
        }
    }
}