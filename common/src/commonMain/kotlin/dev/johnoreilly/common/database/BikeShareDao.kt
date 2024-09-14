package dev.johnoreilly.common.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.johnoreilly.common.model.Network
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeShareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworkList(networkList: List<Network>)

    @Query("SELECT * FROM Network")
    fun getNetworkListAsFlow(): Flow<List<Network>>
}
