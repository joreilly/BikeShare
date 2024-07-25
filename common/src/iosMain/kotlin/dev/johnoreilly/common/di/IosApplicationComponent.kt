package dev.johnoreilly.common.di

import me.tatarka.inject.annotations.Component


@Component
@Singleton
abstract class IosApplicationComponent: SharedApplicationComponent {

    companion object
}