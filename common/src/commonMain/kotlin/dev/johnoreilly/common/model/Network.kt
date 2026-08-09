package dev.johnoreilly.common.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class Network(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)
