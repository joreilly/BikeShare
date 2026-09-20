package dev.johnoreilly.common.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.remote.freeBikes
import dev.johnoreilly.common.stationlist.availabilityColor

/**
 * The stations of a network plotted on an OpenStreetMap basemap, each marker coloured by how
 * many bikes are free there — the same scale the list rows use.
 *
 * The map frames the whole network the first time it has both stations and a size to work with,
 * and then leaves the camera alone so that the thirty-second refresh never yanks the view back
 * from wherever the user has panned to.
 */
@Composable
fun StationMap(stations: List<Station>, modifier: Modifier = Modifier) {
    val camera = rememberMapCameraState()
    var selectedStation by remember { mutableStateOf<Station?>(null) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val viewportSize = with(density) { Size(maxWidth.toPx(), maxHeight.toPx()) }

        var hasFramedNetwork by remember { mutableStateOf(false) }
        LaunchedEffect(stations.isNotEmpty(), viewportSize) {
            if (hasFramedNetwork || camera.movedByUser || stations.isEmpty()) return@LaunchedEffect
            val bounds = fitBounds(
                points = stations.map { LatLon(it.latitude, it.longitude) },
                viewportSize = viewportSize,
                density = density.density,
                paddingPx = with(density) { FRAMING_PADDING.toPx() },
            ) ?: return@LaunchedEffect
            val (latitude, longitude, zoom) = bounds
            camera.moveTo(latitude, longitude, zoom.coerceAtMost(MAX_FRAMING_ZOOM))
            hasFramedNetwork = true
        }

        // The selected station is looked up again on every refresh so the card keeps counting
        // down with the rest of the screen rather than freezing at the moment it was tapped.
        val selected = remember(stations, selectedStation) {
            selectedStation?.let { chosen -> stations.firstOrNull { it.isSameStationAs(chosen) } }
        }

        OsmMap(
            camera = camera,
            modifier = Modifier.fillMaxSize(),
            onTap = { projection, position ->
                selectedStation = stations.nearestTo(position, projection, with(density) { TAP_RADIUS.toPx() })
            },
        ) { projection ->
            val markerRadius = MARKER_RADIUS.toPx()
            val cullMargin = markerRadius * 3f

            fun drawStation(station: Station, isSelected: Boolean) {
                val position = projection.project(station.latitude, station.longitude)
                if (position.x < -cullMargin || position.x > size.width + cullMargin) return
                if (position.y < -cullMargin || position.y > size.height + cullMargin) return

                val radius = if (isSelected) markerRadius * 1.45f else markerRadius
                drawCircle(Color.Black.copy(alpha = 0.18f), radius + 1.5f, position.copy(y = position.y + 1f))
                drawCircle(Color.White, radius, position)
                drawCircle(station.availabilityColor(), radius - MARKER_RING_WIDTH.toPx(), position)
            }

            stations.forEach { station ->
                if (!station.isSameStationAs(selected)) drawStation(station, isSelected = false)
            }
            // Drawn last so it sits above its neighbours in a dense city centre.
            selected?.let { drawStation(it, isSelected = true) }
        }

        selected?.let { station ->
            SelectedStationCard(
                station = station,
                onDismiss = { selectedStation = null },
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).fillMaxWidth(0.8f),
            )
        }
    }
}

@Composable
private fun SelectedStationCard(station: Station, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 8.dp, end = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = "Available bikes",
                        tint = station.availabilityColor(),
                    )
                    Text(
                        text = station.freeBikes().toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = station.availabilityColor(),
                    )
                    Icon(
                        Icons.Default.LocalParking,
                        contentDescription = "Empty slots",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = (station.empty_slots ?: 0).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss station details")
            }
        }
    }
}

/**
 * The station whose marker is closest to [position], or null if the tap landed on open map.
 *
 * Stations are matched in screen space rather than by geography so the touch target stays the
 * same size however far the map is zoomed out.
 */
private fun List<Station>.nearestTo(
    position: Offset,
    projection: MapProjection,
    radiusPx: Float,
): Station? {
    var nearest: Station? = null
    var nearestDistanceSquared = radiusPx * radiusPx
    forEach { station ->
        val marker = projection.project(station.latitude, station.longitude)
        val distanceSquared = (marker - position).getDistanceSquared()
        if (distanceSquared <= nearestDistanceSquared) {
            nearest = station
            nearestDistanceSquared = distanceSquared
        }
    }
    return nearest
}

/**
 * The polled station feed hands back fresh [Station] instances every thirty seconds, so identity
 * is taken from the station's id — falling back to its name for the networks that omit ids.
 */
private fun Station.isSameStationAs(other: Station?): Boolean {
    if (other == null) return false
    val id = id
    return if (!id.isNullOrEmpty()) id == other.id else name == other.name
}

private val MARKER_RADIUS = 7.dp
private val MARKER_RING_WIDTH = 2.dp
private val TAP_RADIUS = 20.dp
private val FRAMING_PADDING = 32.dp

/** Even a compact network should not open zoomed further in than a few streets. */
private const val MAX_FRAMING_ZOOM = 16f
