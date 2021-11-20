package com.surrus.bikeshare

import android.app.Application
import com.surrus.bikeshare.di.appModule
import com.surrus.common.appContext
import com.surrus.common.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class BikeShareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this

        initKoin {
            // https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BikeShareApplication)
            modules(appModule)
        }
    }
}