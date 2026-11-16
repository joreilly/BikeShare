package dev.johnoreilly.bikeshare.functions

import dev.johnoreilly.common.model.Network
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkQueryTest {

    private val networks = listOf(
        Network("dublinbikes", "dublinbikes", "Dublin", "IE", 53.35, -6.26),
        Network("bleeperbike", "Bleeper", "Dublin", "IE", 53.35, -6.26),
        Network("wienmobil-rad", "WienMobil Rad", "Wien", "AT", 48.2, 16.37),
        Network("velib", "Velib' Métropole", "Paris", "FR", 48.86, 2.35),
        Network("citi-bike-nyc", "Citi Bike", "New York, NY", "US", 40.71, -74.0),
    )

    private fun matching(query: String?) = networks.filter { it.matchesQuery(query) }.map { it.id }

    @Test
    fun countryMatchesByNameOrCode() {
        assertEquals(listOf("dublinbikes", "bleeperbike"), matching("Ireland"))
        assertEquals(listOf("dublinbikes", "bleeperbike"), matching("ie"))
        assertEquals(listOf("velib"), matching("France"))
    }

    @Test
    fun twoLetterQueryIsOnlyACountryCode() {
        // Used to match Wien by substring
        assertEquals(emptyList<String>(), matching("XX"))
        assertEquals(listOf("wienmobil-rad"), matching("AT"))
    }

    @Test
    fun namesMatchIgnoringSpacesAndPunctuation() {
        // Gemini Nano asked for "Dublin Bikes"; the network is "dublinbikes". Dublin's other
        // network matches too, by city, which is fine: the model sees both names.
        assertEquals(listOf("dublinbikes", "bleeperbike"), matching("Dublin Bikes"))
        assertEquals(listOf("velib"), matching("Velib'"))
    }

    @Test
    fun aNameOrCityWithinALongerQueryMatches() {
        assertEquals(listOf("velib"), matching("bikes in Paris"))
        // Gemini Nano asked for "Citi Bike New York"
        assertEquals(listOf("citi-bike-nyc"), matching("Citi Bike New York"))
        assertEquals(listOf("citi-bike-nyc"), matching("New York"))
    }

    @Test
    fun nameAndCityStillMatchBySubstring() {
        assertEquals(listOf("dublinbikes", "bleeperbike"), matching("dubl"))
        assertEquals(listOf("velib"), matching("Velib"))
        assertEquals(networks.map { it.id }, matching(null))
        assertEquals(networks.map { it.id }, matching("  "))
    }
}
