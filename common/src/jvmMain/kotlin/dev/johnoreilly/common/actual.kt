package dev.johnoreilly.common

import io.ktor.client.engine.java.*
import org.koin.dsl.module


actual fun platformModule() = module {
    single { Java.create() }
}

