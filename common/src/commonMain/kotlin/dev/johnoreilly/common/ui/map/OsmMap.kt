package dev.johnoreilly.common.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.pow

/**
 * A slippy map drawn straight onto a Compose canvas from OpenStreetMap raster tiles.
 *
 * Everything here is common code, so the same map runs on Android, iOS, desktop and web with no
 * platform map SDK behind it. Callers supply markers through [overlay], which is handed the
 * frame's [MapProjection] so it can turn coordinates into pixels.
 */
@Composable
fun OsmMap(
    camera: MapCameraState,
    modifier: Modifier = Modifier,
    onTap: (MapProjection, Offset) -> Unit = { _, _ -> },
    overlay: DrawScope.(MapProjection) -> Unit = {},
) {
    val loader = LocalTileLoader.current
    val density = LocalDensity.current.density
    var viewportSize by remember { mutableStateOf(Size.Zero) }

    fun projection() = MapProjection(
        centerUnitX = lonToUnitX(camera.longitude),
        centerUnitY = latToUnitY(camera.latitude),
        worldSizePx = worldSizePx(camera.zoom, density),
        viewportSize = viewportSize,
    )

    // Only the *set* of visible tiles is tracked here, not where each one lands: the positions
    // shift on every sub-pixel pan, and re-running the loader that often would be pure noise.
    val visibleTiles by remember(density) {
        derivedStateOf {
            if (viewportSize == Size.Zero) {
                emptyList()
            } else {
                projection().visibleTileKeys()
            }
        }
    }

    if (loader != null) {
        LaunchedEffect(loader) {
            snapshotFlow { visibleTiles }.collect { keys ->
                keys.forEach { key -> launch { loader.prefetch(key) } }
            }
        }
    }

    Box(modifier.clipToBounds().background(MaterialTheme.colorScheme.surfaceVariant)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { viewportSize = it.toSize() }
                .pointerInput(density) {
                    detectTransformGestures { centroid, pan, zoomChange, _ ->
                        camera.applyGesture(viewportSize, density, centroid, pan, zoomChange)
                    }
                }
                .pointerInput(density) {
                    detectTapGestures { position -> onTap(projection(), position) }
                }
                .pointerInput(density) {
                    // Mouse wheel / trackpad zoom, which is how desktop and web users expect
                    // to drive a map; touch platforms simply never deliver these events.
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type != PointerEventType.Scroll) continue
                            val change = event.changes.firstOrNull() ?: continue
                            val scroll = change.scrollDelta.y
                            if (scroll != 0f) {
                                camera.applyGesture(
                                    viewportSize = viewportSize,
                                    density = density,
                                    centroid = change.position,
                                    pan = Offset.Zero,
                                    zoomChange = 2f.pow(-scroll * SCROLL_ZOOM_STEP),
                                )
                                change.consume()
                            }
                        }
                    }
                }
        ) {
            // Reading the revision inside the draw block is what makes an arriving tile
            // invalidate the canvas.
            loader?.revision

            val projection = MapProjection(
                centerUnitX = lonToUnitX(camera.longitude),
                centerUnitY = latToUnitY(camera.latitude),
                worldSizePx = worldSizePx(camera.zoom, density),
                viewportSize = size,
            )

            if (loader != null) {
                projection.forEachVisibleTile { key, left, top ->
                    drawTile(loader, key, left, top, projection.tileSizePx)
                }
            }

            overlay(projection)
        }

        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
        ) {
            FilledTonalIconButton(onClick = { camera.zoomBy(1f) }) {
                Icon(Icons.Default.Add, contentDescription = "Zoom in")
            }
            FilledTonalIconButton(onClick = { camera.zoomBy(-1f) }) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom out")
            }
        }

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
    }
}

/** Device pixels spanned by the whole world at [zoom]. */
fun worldSizePx(zoom: Float, density: Float): Double =
    TILE_SIZE * density * 2.0.pow(zoom.toDouble())

/**
 * Walks the tile grid currently on screen, north-west to south-east.
 *
 * Tile columns wrap around the antimeridian, so [action] receives the wrapped key together with
 * the unwrapped position it should be drawn at. Rows do not wrap — there is nothing above the
 * north pole — so out-of-range rows are simply skipped.
 */
private inline fun MapProjection.forEachVisibleTile(
    action: (key: TileKey, left: Double, top: Double) -> Unit,
) {
    val tilesAcross = 1 shl tileZoom
    val originX = unitXToScreen(0.0)
    val originY = unitYToScreen(0.0)

    val firstColumn = floor(-originX / tileSizePx).toInt()
    val lastColumn = floor((viewportSize.width - originX) / tileSizePx).toInt()
    val firstRow = floor(-originY / tileSizePx).toInt().coerceAtLeast(0)
    val lastRow = floor((viewportSize.height - originY) / tileSizePx).toInt()
        .coerceAtMost(tilesAcross - 1)

    for (row in firstRow..lastRow) {
        for (column in firstColumn..lastColumn) {
            val wrappedColumn = column.mod(tilesAcross)
            action(
                TileKey(tileZoom, wrappedColumn, row),
                originX + column * tileSizePx,
                originY + row * tileSizePx,
            )
        }
    }
}

private fun MapProjection.visibleTileKeys(): List<TileKey> {
    val keys = mutableListOf<TileKey>()
    forEachVisibleTile { key, _, _ -> keys.add(key) }
    // Load outwards from the middle, so the part of the map being looked at fills in first.
    val centreColumn = (centerUnitX * (1 shl tileZoom))
    val centreRow = (centerUnitY * (1 shl tileZoom))
    return keys.sortedBy { key ->
        val dx = key.x - centreColumn
        val dy = key.y - centreRow
        dx * dx + dy * dy
    }
}

/**
 * Draws one tile, falling back to a magnified crop of an already-cached ancestor while the real
 * one is still downloading. Without this the map flashes empty on every zoom step.
 */
private fun DrawScope.drawTile(
    loader: TileLoader,
    key: TileKey,
    left: Double,
    top: Double,
    tileSizePx: Double,
) {
    val dstOffset = IntOffset(floor(left).toInt(), floor(top).toInt())
    val dstSize = IntSize(
        ceil(left + tileSizePx).toInt() - dstOffset.x,
        ceil(top + tileSizePx).toInt() - dstOffset.y,
    )

    loader.peek(key)?.let { image ->
        drawImage(image = image, dstOffset = dstOffset, dstSize = dstSize)
        return
    }

    for (depth in 1..MAX_ANCESTOR_FALLBACK_DEPTH) {
        val ancestorZoom = key.zoom - depth
        if (ancestorZoom < 0) return
        val ancestor = loader.peek(TileKey(ancestorZoom, key.x shr depth, key.y shr depth))
            ?: continue

        val span = ancestor.width shr depth
        if (span < 1) return
        val mask = (1 shl depth) - 1
        drawImage(
            image = ancestor,
            srcOffset = IntOffset((key.x and mask) * span, (key.y and mask) * span),
            srcSize = IntSize(span, span),
            dstOffset = dstOffset,
            dstSize = dstSize,
        )
        return
    }
}

/**
 * Applies one step of a pan/zoom gesture, keeping the geography under [centroid] pinned to
 * [centroid] + [pan] afterwards — which is what makes a pinch feel like it is stretching the map
 * rather than moving the camera independently of the fingers.
 */
private fun MapCameraState.applyGesture(
    viewportSize: Size,
    density: Float,
    centroid: Offset,
    pan: Offset,
    zoomChange: Float,
) {
    if (viewportSize == Size.Zero) return

    val currentWorldSize = worldSizePx(zoom, density)
    val anchorUnitX = lonToUnitX(longitude) + (centroid.x - viewportSize.width / 2.0) / currentWorldSize
    val anchorUnitY = latToUnitY(latitude) + (centroid.y - viewportSize.height / 2.0) / currentWorldSize

    val newZoom = if (zoomChange > 0f) {
        (zoom + log2(zoomChange)).coerceIn(MIN_ZOOM, MAX_ZOOM)
    } else {
        zoom
    }
    val newWorldSize = worldSizePx(newZoom, density)

    val targetX = centroid.x + pan.x
    val targetY = centroid.y + pan.y
    var centerUnitX = anchorUnitX - (targetX - viewportSize.width / 2.0) / newWorldSize
    val centerUnitY = (anchorUnitY - (targetY - viewportSize.height / 2.0) / newWorldSize)
        .coerceIn(0.0, 1.0)

    centerUnitX -= floor(centerUnitX) // wrap around the antimeridian rather than hitting a wall

    longitude = unitXToLon(centerUnitX)
    latitude = unitYToLat(centerUnitY)
    zoom = newZoom
    movedByUser = true
}

/** How much one notch of scroll changes the zoom level. */
private const val SCROLL_ZOOM_STEP = 0.25f

/** Three levels up is a 8x magnified crop — blurry, but better than a hole in the map. */
private const val MAX_ANCESTOR_FALLBACK_DEPTH = 3
