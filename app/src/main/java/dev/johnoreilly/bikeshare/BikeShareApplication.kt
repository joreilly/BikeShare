package dev.johnoreilly.bikeshare

import android.app.Application
import dev.johnoreilly.bikeshare.di.appModule
import dev.johnoreilly.common.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class BikeShareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            // https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BikeShareApplication)
            modules(appModule)
        }
    }
}