package dev.johnoreilly.common.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.sinh
import kotlin.math.tan

/** Raster tiles served by OpenStreetMap are 256x256. */
const val TILE_SIZE = 256

/** The Web Mercator projection is undefined at the poles; it is cut off here instead. */
const val MAX_LATITUDE = 85.05112878

/** Zoom is expressed in density independent pixels: the world is 256.dp wide at zoom 0. */
const val MIN_ZOOM = 1f
const val MAX_ZOOM = 18f

/** The deepest tile pyramid level OpenStreetMap publishes. */
const val MAX_TILE_ZOOM = 19

data class LatLon(val latitude: Double, val longitude: Double)

// Web Mercator, normalised so the whole world is the unit square: (0, 0) is its north-west
// corner and (1, 1) its south-east one. Keeping the projection independent of zoom means the
// tile grid, the marker positions and the gesture handling all share one coordinate space, and
// only the final multiplication by the world's on-screen size brings zoom into it.

fun lonToUnitX(longitude: Double): Double = (longitude + 180.0) / 360.0

fun latToUnitY(latitude: Double): Double {
    val radians = latitude.coerceIn(-MAX_LATITUDE, MAX_LATITUDE) * PI / 180.0
    return (1.0 - ln(tan(radians) + 1.0 / cos(radians)) / PI) / 2.0
}

fun unitXToLon(x: Double): Double = x * 360.0 - 180.0

fun unitYToLat(y: Double): Double = atan(sinh(PI * (1.0 - 2.0 * y))) * 180.0 / PI

/**
 * Maps between geographic coordinates and viewport pixels for a single frame.
 *
 * [worldSizePx] is how wide the whole world is in device pixels, so it reaches ~2e8 at high
 * zoom on a dense screen — far past where a Float can resolve individual pixels. Every
 * conversion therefore works on the *offset from the centre* in Double and narrows to Float
 * only once the result is back down to viewport scale.
 */
class MapProjection(
    val centerUnitX: Double,
    val centerUnitY: Double,
    val worldSizePx: Double,
    val viewportSize: Size,
) {
    /** Tile pyramid level whose pixels line up most closely with the screen's. */
    val tileZoom: Int = floor(log2(worldSizePx / TILE_SIZE)).toInt().coerceIn(0, MAX_TILE_ZOOM)

    /** On-screen size of one tile at [tileZoom], in device pixels. */
    val tileSizePx: Double = worldSizePx / (1 shl tileZoom)

    fun unitXToScreen(x: Double): Double = viewportSize.width / 2.0 + (x - centerUnitX) * worldSizePx

    fun unitYToScreen(y: Double): Double = viewportSize.height / 2.0 + (y - centerUnitY) * worldSizePx

    fun project(latitude: Double, longitude: Double): Offset = Offset(
        unitXToScreen(lonToUnitX(longitude)).toFloat(),
        unitYToScreen(latToUnitY(latitude)).toFloat(),
    )

    fun unproject(point: Offset): LatLon = LatLon(
        latitude = unitYToLat(centerUnitY + (point.y - viewportSize.height / 2.0) / worldSizePx),
        longitude = unitXToLon(centerUnitX + (point.x - viewportSize.width / 2.0) / worldSizePx),
    )
}

/**
 * Picks the camera position that frames every one of [points], leaving [paddingPx] of margin.
 *
 * Returns null when there is nothing to frame or the viewport has no size yet.
 */
fun fitBounds(
    points: List<LatLon>,
    viewportSize: Size,
    density: Float,
    paddingPx: Float,
): Triple<Double, Double, Float>? {
    if (points.isEmpty() || viewportSize.width <= 0f || viewportSize.height <= 0f) return null

    var minUnitX = Double.MAX_VALUE
    var maxUnitX = -Double.MAX_VALUE
    var minUnitY = Double.MAX_VALUE
    var maxUnitY = -Double.MAX_VALUE
    for (point in points) {
        val unitX = lonToUnitX(point.longitude)
        val unitY = latToUnitY(point.latitude)
        if (unitX < minUnitX) minUnitX = unitX
        if (unitX > maxUnitX) maxUnitX = unitX
        if (unitY < minUnitY) minUnitY = unitY
        if (unitY > maxUnitY) maxUnitY = unitY
    }

    val availableWidth = (viewportSize.width - 2 * paddingPx).coerceAtLeast(1f)
    val availableHeight = (viewportSize.height - 2 * paddingPx).coerceAtLeast(1f)
    val spanUnitX = maxUnitX - minUnitX
    val spanUnitY = maxUnitY - minUnitY

    // A single station (or a perfectly aligned row of them) has no extent in one or both axes,
    // which would otherwise ask for infinite zoom.
    val worldSizeForWidth = if (spanUnitX > 0) availableWidth / spanUnitX else Double.MAX_VALUE
    val worldSizeForHeight = if (spanUnitY > 0) availableHeight / spanUnitY else Double.MAX_VALUE
    val worldSizePx = minOf(worldSizeForWidth, worldSizeForHeight)

    val zoom = if (worldSizePx == Double.MAX_VALUE) {
        DEFAULT_SINGLE_POINT_ZOOM
    } else {
        log2(worldSizePx / (TILE_SIZE * density)).toFloat()
    }

    return Triple(
        unitYToLat((minUnitY + maxUnitY) / 2.0),
        unitXToLon((minUnitX + maxUnitX) / 2.0),
        zoom.coerceIn(MIN_ZOOM, MAX_ZOOM),
    )
}

private const val DEFAULT_SINGLE_POINT_ZOOM = 15f
