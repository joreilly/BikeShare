package dev.johnoreilly.bikeshare.functions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionDeclaration
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import dev.johnoreilly.bikeshare.BikeShareApplication
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.remote.freeBikes
import dev.johnoreilly.common.remote.slots
import dev.johnoreilly.common.repository.CityBikesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import java.util.Locale
import android.location.Address
import android.location.Geocoder
import androidx.appfunctions.AppFunctionNotSupportedException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * AppFunctions for BikeShare. KSP generates the concrete [BikeShareAppFunctionService]
 * registered in the manifest.
 */
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "BikeShareAppFunctionService",
    appFunctionXmlFileName = "bikeshare_app_function_service",
)
abstract class BaseBikeShareAppFunctionService : AppFunctionService() {
    private val repository: CityBikesRepository by lazy {
        (applicationContext as BikeShareApplication).component.cityBikesRepository
    }

    /**
     * Lists the bike share networks (schemes) that BikeShare knows about, worldwide.
     *
     * @param query Text to match against a network's name or city, or a country's name or code
     *   (for example "Dublin", "Velib", "France" or "FR"), ignoring case. Omit it to list every network.
     * @return The matching networks, with the ID that the station functions take.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun listNetworks(query: String? = null): List<NetworkResponse> =
        withContext(Dispatchers.IO) {
            val networks = load("the list of bike share networks") { repository.getNetworkList() }
            networks
                .filter { it.matchesQuery(query) }
                .sortedWith(compareBy({ it.country }, { it.city }))
                .map { it.toResponse() }
        }

    /**
     * Lists the stations of a bike share network with their current number of free bikes and
     * empty docks.
     *
     * @param networkId ID of the network, as returned in [NetworkResponse.id].
     * @param query Text to match against a station's name, ignoring case, such as a street or
     *   landmark. Omit it to list every station in the network.
     * @param sortBy "freeBikes" or "emptyDocks" to list the stations with the most of those first,
     *   for questions like "which stations have the most bikes". Omit it to sort by name.
     * @param limit Maximum number of stations to return. Omit it to return them all; use a small
     *   limit, such as 5, with sortBy.
     * @return The matching stations.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun listStations(
        networkId: String,
        query: String? = null,
        sortBy: String? = null,
        limit: Int = 0,
    ): List<StationResponse> =
        withContext(Dispatchers.IO) {
            stations(networkId)
                .selectStations(query, sortBy, limit)
                .map { it.toResponse(distanceMetres = null) }
        }

    /**
     * Finds the stations of a bike share network closest to a location, with their current
     * number of free bikes and empty docks. Give the location as a place name, or as latitude
     * and longitude when they're already known (for example from a station).
     *
     * @param networkId ID of the network, as returned in [NetworkResponse.id].
     * @param place Name or address of the place to search around, such as a landmark, street or
     *   area ("Tower Bridge", "Grafton Street"). The app looks up where it is, so use this for
     *   named places instead of guessing coordinates.
     * @param latitude Latitude of the location to search around, in degrees. Only when known.
     * @param longitude Longitude of the location to search around, in degrees. Only when known.
     * @param minFreeBikes Only include stations with at least this many free bikes. Omit it to
     *   include every station.
     * @param minEmptyDocks Only include stations with at least this many empty docks, for
     *   returning a bike. Omit it to include every station.
     * @param limit Maximum number of stations to return, from 1 to 20. Defaults to 5.
     * @return The nearest matching stations, closest first, with their distance from the location.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun findNearbyStations(
        networkId: String,
        place: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        minFreeBikes: Int = 0,
        minEmptyDocks: Int = 0,
        limit: Int = 0,
    ): List<StationResponse> = withContext(Dispatchers.IO) {
        val (lat, lon) = when {
            !place.isNullOrBlank() -> locate(place, networkId)
            latitude != null && longitude != null -> {
                if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
                    throw AppFunctionInvalidArgumentException(
                        "Latitude must be between -90 and 90, and longitude between -180 and 180."
                    )
                }
                latitude to longitude
            }
            else -> throw AppFunctionInvalidArgumentException(
                "Give a place name, or both latitude and longitude."
            )
        }
        // An omitted limit arrives as 0, not the declared default.
        val count = if (limit <= 0) DEFAULT_NEARBY_LIMIT else limit.coerceAtMost(MAX_NEARBY_LIMIT)
        stations(networkId)
            .filter { it.freeBikes() >= minFreeBikes && (it.empty_slots ?: 0) >= minEmptyDocks }
            .map { it to distanceMetres(lat, lon, it.latitude, it.longitude) }
            .sortedBy { it.second }
            .take(count)
            .map { (station, distance) -> station.toResponse(distance) }
    }

    /**
     * Where [place] is, looked up with [Geocoder] near the network first, so "Tower Bridge" is the
     * one in London for Santander Cycles. Models given only a name guess coordinates, and in
     * testing Gemini Nano put "near Tower Bridge" in central London, about 3.5km out.
     */
    private suspend fun locate(place: String, networkId: String): Pair<Double, Double> {
        if (!Geocoder.isPresent()) {
            throw AppFunctionNotSupportedException(
                "Looking up places isn't available on this device. Give latitude and longitude instead."
            )
        }
        val network = load("the list of bike share networks") { repository.getNetworkList() }
            .firstOrNull { it.id == networkId }
        val geocoder = Geocoder(applicationContext)
        val address = network?.let {
            geocoder.find(
                place,
                it.latitude - NEARBY_DEGREES, it.longitude - NEARBY_DEGREES,
                it.latitude + NEARBY_DEGREES, it.longitude + NEARBY_DEGREES,
            )
        } ?: geocoder.find(place)
            ?: throw AppFunctionElementNotFoundException("Couldn't find a place called '$place'.")
        if (network != null &&
            distanceMetres(network.latitude, network.longitude, address.latitude, address.longitude) > MAX_PLACE_DISTANCE_METRES
        ) {
            throw AppFunctionElementNotFoundException(
                "'$place' was found, but not near the ${network.name} network in ${network.city}."
            )
        }
        return address.latitude to address.longitude
    }

    private suspend fun Geocoder.find(name: String, vararg box: Double): Address? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) =
                    continuation.resume(addresses.firstOrNull())
                override fun onError(errorMessage: String?) = continuation.resume(null)
            }
            if (box.size == 4) {
                getFromLocationName(name, 1, box[0], box[1], box[2], box[3], listener)
            } else {
                getFromLocationName(name, 1, listener)
            }
        }

    private suspend fun stations(networkId: String): List<Station> {
        if (networkId.isBlank()) {
            throw AppFunctionInvalidArgumentException(
                "A networkId is required. Use listNetworks to find it."
            )
        }
        return load("the stations of network '$networkId'") {
            repository.fetchBikeShareInfoOrNull(networkId)
        } ?: throw AppFunctionElementNotFoundException(
            "No bike share network with ID '$networkId'. Use listNetworks to find valid IDs."
        )
    }

    /**
     * Runs [block], turning a failure to reach the CityBikes API into an error the agent can
     * relay, rather than letting it surface as an unexplained failure or an empty result.
     */
    private suspend fun <T> load(what: String, block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("AppFunctions: loading $what failed: $e")
            throw AppFunctionAppUnknownException(
                "Couldn't load $what from the CityBikes service. Check the connection and try again."
            )
        }

    private fun Network.toResponse() = NetworkResponse(
        id = id,
        name = name,
        city = city,
        country = country,
        latitude = latitude,
        longitude = longitude,
    )

    private fun Station.toResponse(distanceMetres: Int?) = StationResponse(
        id = id.orEmpty(),
        name = name,
        freeBikes = freeBikes(),
        emptyDocks = empty_slots ?: 0,
        totalDocks = slots(),
        latitude = latitude,
        longitude = longitude,
        distanceMetres = distanceMetres,
    )

    private companion object {
        const val DEFAULT_NEARBY_LIMIT = 5
        const val MAX_NEARBY_LIMIT = 20
        const val EARTH_RADIUS_METRES = 6_371_000.0

        // Place lookups search this far (in degrees, roughly 30km) around the network first,
        // and accept a place this far from the network's centre at most.
        const val NEARBY_DEGREES = 0.3
        const val MAX_PLACE_DISTANCE_METRES = 50_000

        // Great-circle (haversine) distance, rounded to the metre.
        fun distanceMetres(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
            return (EARTH_RADIUS_METRES * 2 * atan2(sqrt(a), sqrt(1 - a))).roundToInt()
        }
    }
}

/** A bike share network (scheme), such as Dublin's or Paris's. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class NetworkResponse(
    /** Unique ID of the network, passed to the station functions as networkId. */
    val id: String,
    /** Name of the network, such as "Dublinbikes". */
    val name: String,
    /** City the network operates in. */
    val city: String,
    /** ISO 3166 two-letter code of the country the network operates in, such as "IE". */
    val country: String,
    /** Latitude of the network's location, in degrees. */
    val latitude: Double,
    /** Longitude of the network's location, in degrees. */
    val longitude: Double,
)

/** A bike share station, with its availability when it was fetched. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class StationResponse(
    /** Unique ID of the station within its network. */
    val id: String,
    /** Name of the station, usually its street or landmark. */
    val name: String,
    /** Number of bikes currently available to take from the station. */
    val freeBikes: Int,
    /** Number of docks currently empty, for returning a bike. */
    val emptyDocks: Int,
    /** Total number of docks at the station. */
    val totalDocks: Int,
    /** Latitude of the station, in degrees. */
    val latitude: Double,
    /** Longitude of the station, in degrees. */
    val longitude: Double,
    /** Distance from the searched location in metres, or null when no location was searched. */
    val distanceMetres: Int?,
)

/**
 * Whether a network matches [listNetworks]' query, written for how models phrase them:
 * - countries by code ("IE") or English name ("Ireland"). A two-letter query is a country code
 *   only, so "IE" doesn't also match every city containing "ie" (Wien, Kiel);
 * - names and cities ignoring spaces and punctuation, so "Dublin Bikes" finds "dublinbikes";
 * - a name or city within a longer query: "Citi Bike New York" finds Citi Bike (city
 *   "New York, NY"), "bikes in Paris" finds Paris's networks.
 */
internal fun Network.matchesQuery(query: String?): Boolean {
    val text = query?.trim().orEmpty()
    if (text.isEmpty()) return true
    if (country.equals(text, ignoreCase = true) || countryName(country).equals(text, ignoreCase = true)) return true
    if (text.length <= 2) return false
    val wanted = text.normalized()
    if (name.normalized().contains(wanted) || city.normalized().contains(wanted)) return true
    // "New York, NY" -> "newyork"; short names are skipped so they don't match by accident
    return listOf(name, city.substringBefore(','))
        .map { it.normalized() }
        .any { it.length >= 4 && wanted.contains(it) }
}

private fun String.normalized() = lowercase().filter { it.isLetterOrDigit() }

private fun countryName(code: String): String? =
    runCatching { Locale.Builder().setRegion(code).build().getDisplayCountry(Locale.ENGLISH) }.getOrNull()

/**
 * [listStations]' filtering, ordering and limit. Ranking happens here rather than in the model:
 * Gemini Nano, handed all 113 Dublin stations, named the wrong "top five" and misquoted two counts.
 */
internal fun List<Station>.selectStations(query: String?, sortBy: String?, limit: Int): List<Station> {
    val text = query?.trim().orEmpty()
    val matching = filter { text.isEmpty() || it.name.contains(text, ignoreCase = true) }
    val sorted = when (sortBy?.trim()?.lowercase()) {
        "freebikes", "free_bikes", "bikes" -> matching.sortedWith(compareByDescending<Station> { it.freeBikes() }.thenBy { it.name })
        "emptydocks", "empty_docks", "docks", "emptyslots" ->
            matching.sortedWith(compareByDescending<Station> { it.empty_slots ?: 0 }.thenBy { it.name })
        else -> matching.sortedBy { it.name }
    }
    // An omitted limit arrives as 0, not the declared default.
    return if (limit > 0) sorted.take(limit) else sorted
}
