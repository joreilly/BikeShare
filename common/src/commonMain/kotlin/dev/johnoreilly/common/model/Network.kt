package dev.johnoreilly.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Network(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)
