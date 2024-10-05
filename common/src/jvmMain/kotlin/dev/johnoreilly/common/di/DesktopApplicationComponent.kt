package dev.johnoreilly.common.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.database.dbFileName
import dev.johnoreilly.common.ui.BikeShareApp
import io.ktor.client.engine.java.Java
import me.tatarka.inject.annotations.Component
import java.io.File

@Component
@Singleton
abstract class DesktopApplicationComponent: SharedApplicationComponent {
  abstract val bikeShareApp: BikeShareApp

  override fun getHttpClientEngine() = Java.create()

  override fun getRoomDatabase() = createRoomDatabase()

  companion object
}

fun createRoomDatabase(): AppDatabase {
  val dbFile = File(System.getProperty("java.io.tmpdir"), dbFileName)
  return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath,)
    .setDriver(BundledSQLiteDriver())
    .build()
}