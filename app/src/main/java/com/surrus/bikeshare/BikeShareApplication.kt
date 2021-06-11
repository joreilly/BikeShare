package com.surrus.bikeshare

import android.app.Application
import com.surrus.bikeshare.di.appModule
import com.surrus.common.appContext
import com.surrus.common.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BikeShareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this

        initKoin {
            androidLogger()
            androidContext(this@BikeShareApplication)
            modules(appModule)
        }
    }
}