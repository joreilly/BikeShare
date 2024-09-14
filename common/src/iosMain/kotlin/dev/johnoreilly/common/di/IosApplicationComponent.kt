package dev.johnoreilly.common.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.database.dbFileName
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Component
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask


@Component
@Singleton
abstract class IosApplicationComponent: SharedApplicationComponent {

    override fun getHttpClientEngine() = Darwin.create()

    override fun getRoomDatabase() = createRoomDatabase()

    companion object
}

fun createRoomDatabase(): AppDatabase {
    val dbFile = "${fileDirectory()}/$dbFileName"
    return Room.databaseBuilder<AppDatabase>(name = dbFile)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

private fun fileDirectory(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory).path!!
}