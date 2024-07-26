package dev.johnoreilly.common.di

import io.ktor.client.engine.java.Java
import me.tatarka.inject.annotations.Component

@Component
@Singleton
abstract class DesktopApplicationComponent: SharedApplicationComponent {

  override fun getHttpClientEngine() = Java.create()

  companion object
}
