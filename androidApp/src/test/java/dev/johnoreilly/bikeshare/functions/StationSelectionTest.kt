package dev.johnoreilly.bikeshare.functions

import dev.johnoreilly.common.remote.Station
import org.junit.Assert.assertEquals
import org.junit.Test

class StationSelectionTest {

    private fun station(name: String, bikes: Int, docks: Int) =
        Station(name = name, free_bikes = bikes, empty_slots = docks, latitude = 0.0, longitude = 0.0)

    private val stations = listOf(
        station("Newman House", 24, 6),
        station("Charlemont Place", 27, 3),
        station("Parkgate Street", 35, 3),
        station("Custom House", 29, 1),
        station("James Street", 29, 11),
    )

    private fun names(query: String? = null, sortBy: String? = null, limit: Int = 0) =
        stations.selectStations(query, sortBy, limit).map { it.name }

    @Test
    fun mostFreeBikesFirstWithTiesByName() {
        assertEquals(listOf("Parkgate Street", "Custom House", "James Street"), names(sortBy = "freeBikes", limit = 3))
    }

    @Test
    fun mostEmptyDocksFirst() {
        assertEquals(listOf("James Street", "Newman House"), names(sortBy = "emptyDocks", limit = 2))
    }

    @Test
    fun defaultIsEveryMatchingStationByName() {
        assertEquals(stations.map { it.name }.sorted(), names())
        assertEquals(listOf("Charlemont Place"), names(query = "charle"))
        assertEquals(stations.map { it.name }.sorted(), names(sortBy = "nonsense"))
    }
}
