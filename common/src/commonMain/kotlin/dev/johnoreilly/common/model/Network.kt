package dev.johnoreilly.common.model

data class Network(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)
