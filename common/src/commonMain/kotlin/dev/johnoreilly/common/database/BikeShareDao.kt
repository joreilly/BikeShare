package dev.johnoreilly.common.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import dev.johnoreilly.common.model.Network
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeShareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworkList(networkList: List<Network>)

    @Query("SELECT * FROM Network")
    fun getNetworkListAsFlow(): Flow<List<Network>>
}
