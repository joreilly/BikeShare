package dev.johnoreilly.common.di

import androidx.room3.Room
import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.sqlitewasmworker.createSQLiteWasmWorker
import io.ktor.client.engine.js.Js
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class WebApplicationComponent : SharedApplicationComponent {
    override fun httpClientEngine() = Js.create()

    override fun appDatabase() = createRoomDatabase()
}

fun createRoomDatabase(): AppDatabase {
    // In-memory only: GitHub Pages can't serve the COOP/COEP headers OPFS persistence requires.
    return Room.inMemoryDatabaseBuilder<AppDatabase>()
        .setDriver(createSQLiteWasmWorker())
        .build()
}
