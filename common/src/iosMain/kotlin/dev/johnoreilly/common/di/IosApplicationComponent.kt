package dev.johnoreilly.common.di

import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Component


@Component
@Singleton
abstract class IosApplicationComponent: SharedApplicationComponent {

    override fun getHttpClientEngine() = Darwin.create()

    companion object
}