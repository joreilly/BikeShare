package dev.johnoreilly.common.di

import me.tatarka.inject.annotations.Component

@Component
@Singleton
abstract class DesktopApplicationComponent: SharedApplicationComponent {

  companion object
}
