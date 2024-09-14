package dev.johnoreilly.common.database

import androidx.room.*
import dev.johnoreilly.common.model.Network

internal expect object AppDatabaseCtor : RoomDatabaseConstructor<AppDatabase>

@Database(entities = [Network::class], version = 1)
@ConstructedBy(AppDatabaseCtor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bikeShareDao(): BikeShareDao
}

internal const val dbFileName = "bikeshare.db"

