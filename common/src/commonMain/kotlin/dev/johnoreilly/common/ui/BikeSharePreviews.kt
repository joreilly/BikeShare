package dev.johnoreilly.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.countrylist.CountryView
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.networklist.NetworkList
import dev.johnoreilly.common.networklist.NetworkView
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.stationlist.StationListContent
import dev.johnoreilly.common.stationlist.StationView
import dev.johnoreilly.common.viewmodel.Country
import org.jetbrains.compose.ui.tooling.preview.Preview

// Previews for the reusable pieces of the shared UI: the three row/card composables the country,
// network and station lists are built from, and the two list bodies themselves.
//
// All of them already take plain data and lambdas rather than a ViewModel, so each renders from
// literal sample values with no repository, no Koin and no network — which is what makes them
// previewable at all.

private val SampleStations = listOf(
    Station(id = "1", name = "Grand Canal Dock", empty_slots = 3, free_bikes = 17, latitude = 53.34, longitude = -6.24),
    Station(id = "2", name = "Portobello Harbour", empty_slots = 12, free_bikes = 8, latitude = 53.33, longitude = -6.26),
    Station(id = "3", name = "Smithfield North", empty_slots = 0, free_bikes = 20, latitude = 53.35, longitude = -6.28),
)

private val SampleNetworks = listOf(
    Network(id = "dublinbikes", name = "dublinbikes", city = "Dublin", country = "IE", latitude = 53.35, longitude = -6.26),
    Network(id = "velib", name = "Vélib' Métropole", city = "Paris", country = "FR", latitude = 48.86, longitude = 2.35),
    Network(id = "bicing", name = "Bicing", city = "Barcelona", country = "ES", latitude = 41.39, longitude = 2.17),
)

private val SampleCountries = listOf(
    Country(code = "IE", displayName = "Ireland"),
    Country(code = "FR", displayName = "France"),
    Country(code = "ES", displayName = "Spain"),
)

// ---------------------------------------------------------------------------
// Station
// ---------------------------------------------------------------------------

@Preview
@Composable
fun StationViewMixedPreview() {
    Surface {
        StationView(SampleStations[0])
    }
}

@Preview
@Composable
fun StationViewFullPreview() {
    // Every slot taken: the availability bar at its maximum.
    Surface {
        StationView(SampleStations[2])
    }
}

@Preview
@Composable
fun StationViewEmptyPreview() {
    // No bikes left — the state a rider most needs to be able to read at a glance.
    Surface {
        StationView(
            Station(id = "4", name = "Docklands Empty", empty_slots = 20, free_bikes = 0, latitude = 53.34, longitude = -6.23)
        )
    }
}

@Preview
@Composable
fun StationListContentPreview() {
    Surface {
        StationListContent(SampleStations)
    }
}

// ---------------------------------------------------------------------------
// Network
// ---------------------------------------------------------------------------

@Preview
@Composable
fun NetworkViewPreview() {
    Surface {
        NetworkView(SampleNetworks[0], networkSelected = {})
    }
}

@Preview
@Composable
fun NetworkListPreview() {
    Surface {
        NetworkList(
            networkList = SampleNetworks,
            onNetworkSelected = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---------------------------------------------------------------------------
// Country
// ---------------------------------------------------------------------------

@Preview
@Composable
fun CountryViewPreview() {
    Surface {
        CountryView(SampleCountries[0], countrySelected = {})
    }
}

@Preview
@Composable
fun CountryViewListPreview() {
    Surface {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            SampleCountries.forEach { CountryView(it, countrySelected = {}) }
        }
    }
}

// ---------------------------------------------------------------------------
// Theme catalog
//
// The shared module has no theme wrapper of its own — the Android app supplies BikeShareTheme and
// the desktop/web hosts supply their own — so these render against the ambient MaterialTheme, which
// is exactly what the shared composables above see.
// ---------------------------------------------------------------------------

@Composable
private fun Swatch(name: String, color: Color, onColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(color = color, shape = RoundedCornerShape(6.dp), modifier = Modifier.size(40.dp)) {
            Text(
                text = "Aa",
                color = onColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp),
            )
        }
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
fun ColorSchemeSpecimenPreview() {
    val scheme = MaterialTheme.colorScheme
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Colour scheme", style = MaterialTheme.typography.titleMedium)
            Swatch("primary", scheme.primary, scheme.onPrimary)
            Swatch("primaryContainer", scheme.primaryContainer, scheme.onPrimaryContainer)
            Swatch("secondary", scheme.secondary, scheme.onSecondary)
            Swatch("secondaryContainer", scheme.secondaryContainer, scheme.onSecondaryContainer)
            Swatch("tertiary", scheme.tertiary, scheme.onTertiary)
            Swatch("surface", scheme.surface, scheme.onSurface)
            Swatch("surfaceVariant", scheme.surfaceVariant, scheme.onSurfaceVariant)
            Swatch("error", scheme.error, scheme.onError)
        }
    }
}

@Composable
private fun TypeSample(name: String, style: TextStyle) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(name, style = MaterialTheme.typography.labelSmall)
        Text("Grand Canal Dock", style = style)
    }
}

@Preview
@Composable
fun TypographySpecimenPreview() {
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            val type = MaterialTheme.typography
            TypeSample("headlineMedium", type.headlineMedium)
            TypeSample("titleLarge", type.titleLarge)
            TypeSample("titleMedium", type.titleMedium)
            TypeSample("bodyLarge", type.bodyLarge)
            TypeSample("bodyMedium", type.bodyMedium)
            TypeSample("labelLarge", type.labelLarge)
        }
    }
}
