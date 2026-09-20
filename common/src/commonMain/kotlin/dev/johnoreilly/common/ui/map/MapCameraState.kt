package dev.johnoreilly.common.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Where the map is looking: the geographic point held at the centre of the viewport, and how
 * far in it is zoomed.
 *
 * [zoom] is in density independent units — the whole world is 256.dp across at zoom 0 — so the
 * same value frames the same area on a phone and on a desktop window.
 */
@Stable
class MapCameraState(latitude: Double, longitude: Double, zoom: Float) {
    var latitude by mutableStateOf(latitude.coerceIn(-MAX_LATITUDE, MAX_LATITUDE))
    var longitude by mutableStateOf(longitude)
    var zoom by mutableStateOf(zoom.coerceIn(MIN_ZOOM, MAX_ZOOM))

    /** True once the user has panned or zoomed, so we stop re-framing the map under them. */
    var movedByUser by mutableStateOf(false)
        internal set

    fun moveTo(latitude: Double, longitude: Double, zoom: Float = this.zoom) {
        this.latitude = latitude.coerceIn(-MAX_LATITUDE, MAX_LATITUDE)
        this.longitude = longitude
        this.zoom = zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
    }

    /** Zooms about the centre of the viewport, as the on-screen zoom buttons do. */
    fun zoomBy(delta: Float) {
        zoom = (zoom + delta).coerceIn(MIN_ZOOM, MAX_ZOOM)
        movedByUser = true
    }

    companion object {
        val Saver: Saver<MapCameraState, List<Any>> = Saver(
            save = { listOf(it.latitude, it.longitude, it.zoom, it.movedByUser) },
            restore = {
                MapCameraState(it[0] as Double, it[1] as Double, it[2] as Float).apply {
                    movedByUser = it[3] as Boolean
                }
            },
        )
    }
}

@Composable
fun rememberMapCameraState(
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    zoom: Float = 2f,
): MapCameraState = rememberSaveable(saver = MapCameraState.Saver) {
    MapCameraState(latitude, longitude, zoom)
}
