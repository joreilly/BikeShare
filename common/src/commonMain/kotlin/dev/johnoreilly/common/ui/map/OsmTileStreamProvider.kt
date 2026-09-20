package dev.johnoreilly.common.ui.map

import androidx.compose.runtime.staticCompositionLocalOf
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import ovh.plrapps.mapcompose.core.TileStreamProvider

/**
 * Feeds MapCompose with OpenStreetMap raster tiles over the app's existing Ktor client.
 *
 * MapCompose owns the tile grid, cache and decoding; all this has to do is turn a tile
 * coordinate into bytes. Its zoom levels are numbered the same way OSM numbers them — 0 is the
 * whole world in one tile — so [zoomLvl] passes straight through.
 */
fun osmTileStreamProvider(httpClient: HttpClient) = TileStreamProvider { row, col, zoomLvl ->
    try {
        val bytes = httpClient.get("https://tile.openstreetmap.org/$zoomLvl/$col/$row.png") {
            // The shared client is configured for the CityBikes JSON API; an absolute URL takes
            // precedence over its default host, and asking for PNG keeps its JSON Accept header
            // from speaking for this request.
            accept(ContentType.Image.PNG)
            // OpenStreetMap's tile usage policy requires a request to identify the app making
            // it. Browsers refuse to let scripts set this and drop it, which is fine — there
            // the Referer identifies us instead.
            header(HttpHeaders.UserAgent, OSM_USER_AGENT)
        }.readRawBytes()
        Buffer().apply { write(bytes) } as RawSource
    } catch (_: Exception) {
        // A tile that fails to load simply isn't rendered.
        null
    }
}

const val OSM_USER_AGENT = "BikeShareKMP/1.0 (+https://github.com/joreilly/BikeShare)"

/** OpenStreetMap publishes zoom levels 0..19, so 20 levels in MapCompose's numbering. */
const val OSM_LEVEL_COUNT = 20

/** Width/height of the whole world in pixels at the deepest level: 256 * 2^19. */
const val OSM_FULL_SIZE = 256 shl (OSM_LEVEL_COUNT - 1)

/** OSM's tile usage policy asks clients not to run many downloads in parallel. */
const val OSM_TILE_WORKERS = 4

/**
 * The Ktor client the map draws its basemap from. Provided by `BikeShareApp` so the map shares
 * the application's configured client; where it is absent — Compose previews, for instance —
 * the map still renders its markers, just without a basemap under them.
 */
val LocalHttpClient = staticCompositionLocalOf<HttpClient?> { null }
