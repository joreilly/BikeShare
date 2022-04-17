package com.surrus.common

import io.ktor.client.engine.ios.*
import org.koin.dsl.module


actual fun platformModule() = module {
    single { Ios.create() }
}



