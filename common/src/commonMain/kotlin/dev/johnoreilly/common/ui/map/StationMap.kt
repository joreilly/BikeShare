package dev.johnoreilly.common.ui.map

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.remote.freeBikes
import dev.johnoreilly.common.stationlist.availabilityColor
import ovh.plrapps.mapcompose.api.BoundingBox
import ovh.plrapps.mapcompose.api.addClusterer
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.disableFadeIn
import ovh.plrapps.mapcompose.api.onMarkerClick
import ovh.plrapps.mapcompose.api.removeAllMarkers
import ovh.plrapps.mapcompose.api.snapScrollTo
import ovh.plrapps.mapcompose.api.updateMarkerZ
import ovh.plrapps.mapcompose.ui.MapUI
import ovh.plrapps.mapcompose.ui.state.MapState
import ovh.plrapps.mapcompose.ui.state.markers.model.RenderingStrategy

/**
 * The stations of a network plotted on an OpenStreetMap basemap, each marker coloured by how
 * many bikes are free there — the same scale the list rows use.
 *
 * MapCompose owns the tile pyramid, camera, gestures and marker layer; this file only supplies
 * the tile source, converts station coordinates into its unit-square space, and draws the
 * marker and detail composables.
 */
@Composable
fun StationMap(stations: List<Station>, modifier: Modifier = Modifier) {
    val httpClient = LocalHttpClient.current
    var selectedMarkerId by remember { mutableStateOf<String?>(null) }

    // Markers are added once per network but their composables keep reading the newest station
    // list, so the thirty-second refresh recolours them without rebuilding the layer.
    val latestStations by rememberUpdatedState(stations)

    val mapState = remember {
        MapState(
            levelCount = OSM_LEVEL_COUNT,
            fullWidth = OSM_FULL_SIZE,
            fullHeight = OSM_FULL_SIZE,
            workerCount = OSM_TILE_WORKERS,
        )
    }

    DisposableEffect(mapState) {
        onDispose { mapState.shutdown() }
    }

    LaunchedEffect(mapState, httpClient) {
        if (httpClient != null) mapState.addLayer(osmTileStreamProvider(httpClient))
        mapState.onMarkerClick { id, _, _ -> selectedMarkerId = id }
        // Tiles fade in by advancing their alpha one step per draw pass. On Wasm the canvas
        // stops redrawing as soon as the map is idle, which strands freshly loaded tiles
        // part-way through the fade and leaves the basemap washed out.
        mapState.disableFadeIn()
    }

    // Keyed on the station identities rather than the list, so a refresh that only changes bike
    // counts doesn't tear down and rebuild every marker.
    val stationIds = remember(stations) { stations.map { it.markerId() } }
    LaunchedEffect(mapState, stationIds) {
        mapState.removeAllMarkers()
        if (stationIds.isEmpty()) return@LaunchedEffect

        mapState.addClusterer(STATION_CLUSTERER, clusteringThreshold = 40.dp) { ids ->
            { ClusterBubble(count = ids.size) }
        }

        stations.forEach { station ->
            val markerId = station.markerId()
            mapState.addMarker(
                id = markerId,
                x = lonToUnitX(station.longitude),
                y = latToUnitY(station.latitude),
                relativeOffset = Offset(-0.5f, -0.5f),
                renderingStrategy = RenderingStrategy.Clustering(STATION_CLUSTERER),
            ) {
                val current = latestStations.firstOrNull { it.markerId() == markerId } ?: station
                StationMarker(
                    color = current.availabilityColor(),
                    isSelected = selectedMarkerId == markerId,
                )
            }
        }

        mapState.snapScrollTo(stations.boundingBox(), padding = FRAMING_PADDING)
    }

    // Lift the selected marker above its neighbours so the enlarged one is not hidden behind
    // them in a dense city centre — the canvas renderer did this by drawing it last.
    var raisedMarkerId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(mapState, selectedMarkerId) {
        raisedMarkerId?.let { mapState.updateMarkerZ(it, 0f) }
        selectedMarkerId?.let { mapState.updateMarkerZ(it, 1f) }
        raisedMarkerId = selectedMarkerId
    }

    Box(modifier) {
        MapUI(modifier = Modifier.fillMaxSize(), state = mapState)

        // OpenStreetMap's licence requires the basemap to be credited wherever it is shown.
        Text(
            text = "© OpenStreetMap contributors",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )

        val selected = selectedMarkerId?.let { id -> stations.firstOrNull { it.markerId() == id } }
        selected?.let { station ->
            SelectedStationCard(
                station = station,
                onDismiss = { selectedMarkerId = null },
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).fillMaxWidth(0.8f),
            )
        }
    }
}

@Composable
private fun StationMarker(color: Color, isSelected: Boolean) {
    val size by animateDpAsState(
        targetValue = if (isSelected) MARKER_SIZE * SELECTED_MARKER_SCALE else MARKER_SIZE,
        label = "stationMarkerSize",
    )
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(MARKER_RING_WIDTH, Color.White, CircleShape),
    )
}

@Composable
private fun ClusterBubble(count: Int) {
    Box(
        modifier = Modifier
            .size(CLUSTER_SIZE)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .border(MARKER_RING_WIDTH, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
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
 * The smallest box in MapCompose's unit-square space containing every station.
 *
 * A network with a single station has no extent, which would ask MapCompose to scale in
 * infinitely, so such a box is given a small margin.
 */
private fun List<Station>.boundingBox(): BoundingBox {
    val xs = map { lonToUnitX(it.longitude) }
    val ys = map { latToUnitY(it.latitude) }
    val margin = SINGLE_STATION_MARGIN
    val left = xs.min()
    val right = xs.max()
    val top = ys.min()
    val bottom = ys.max()
    return BoundingBox(
        xLeft = if (right - left > 0) left else left - margin,
        yTop = if (bottom - top > 0) top else top - margin,
        xRight = if (right - left > 0) right else right + margin,
        yBottom = if (bottom - top > 0) bottom else bottom + margin,
    )
}

/**
 * The polled station feed hands back fresh [Station] instances every thirty seconds, so identity
 * is taken from the station's id — falling back to its name for the networks that omit ids.
 */
private fun Station.markerId(): String = id?.takeIf { it.isNotEmpty() } ?: name

private const val STATION_CLUSTERER = "stations"
private val MARKER_SIZE = 14.dp

/** How much the selected marker grows, matching the canvas renderer this replaced. */
private const val SELECTED_MARKER_SCALE = 1.45f
private val CLUSTER_SIZE = 28.dp
private val MARKER_RING_WIDTH = 2.dp

/** Fraction of the viewport left as margin when framing a whole network. */
private val FRAMING_PADDING = Offset(0.15f, 0.15f)

private const val SINGLE_STATION_MARGIN = 0.0005
