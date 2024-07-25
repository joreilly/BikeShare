package dev.johnoreilly.bikeshare

import android.app.Application
import dev.johnoreilly.common.di.AndroidApplicationComponent
import dev.johnoreilly.common.di.create

class BikeShareApplication : Application() {
    val component: AndroidApplicationComponent by lazy {
        AndroidApplicationComponent.create()
    }

    override fun onCreate() {
        super.onCreate()
    }
}