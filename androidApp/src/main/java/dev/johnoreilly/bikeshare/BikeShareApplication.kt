package dev.johnoreilly.bikeshare

import android.app.Application
import dev.johnoreilly.bikeshare.di.appModule
import dev.johnoreilly.common.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BikeShareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@BikeShareApplication)
            modules(appModule)
        }
    }
}