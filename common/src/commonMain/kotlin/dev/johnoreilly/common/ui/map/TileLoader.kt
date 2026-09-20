package dev.johnoreilly.common.ui.map

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import org.jetbrains.compose.resources.decodeToImageBitmap

data class TileKey(val zoom: Int, val x: Int, val y: Int)

/**
 * Downloads OpenStreetMap raster tiles and keeps the recent ones decoded in memory.
 *
 * All of the bookkeeping runs on the caller's dispatcher — which is the main thread, since the
 * only caller is [OsmMap]'s loading effect — and only the fetch and decode are handed off, so
 * the cache needs no locking. Drawing reads it through [peek], which never mutates.
 */
@Stable
class TileLoader(private val httpClient: HttpClient) {
    private val cache = LinkedHashMap<TileKey, ImageBitmap>()
    private val inFlight = mutableSetOf<TileKey>()
    private val downloadPermits = Semaphore(MAX_CONCURRENT_DOWNLOADS)

    /**
     * Incremented whenever a tile lands. [OsmMap] reads it inside its draw block so that an
     * arriving tile invalidates the canvas — the cache itself is deliberately not snapshot
     * state, because a draw pass must be able to read it without recording a dependency.
     */
    var revision by mutableStateOf(0)
        private set

    fun peek(key: TileKey): ImageBitmap? = cache[key]

    /** Fetches [key] unless it is already cached or already being fetched. */
    suspend fun prefetch(key: TileKey) {
        if (cache.containsKey(key) || !inFlight.add(key)) return

        val bitmap = try {
            downloadPermits.withPermit {
                withContext(Dispatchers.Default) {
                    httpClient.get(tileUrl(key)) {
                        // The shared client is configured for the CityBikes JSON API; an
                        // absolute URL takes precedence over its default host, and asking for
                        // PNG keeps its JSON Accept header from speaking for this request.
                        accept(ContentType.Image.PNG)
                        // OpenStreetMap's tile usage policy requires a request to identify the
                        // app making it. Browsers refuse to let scripts set this and drop it,
                        // which is fine — there the Referer identifies us instead.
                        header(HttpHeaders.UserAgent, USER_AGENT)
                    }.readRawBytes().decodeToImageBitmap()
                }
            }
        } catch (cancellation: CancellationException) {
            inFlight.remove(key)
            throw cancellation
        } catch (_: Exception) {
            // A tile that fails to load just stays blank; it will be retried if it scrolls
            // back into view.
            null
        }

        inFlight.remove(key)
        if (bitmap != null) {
            cache[key] = bitmap
            while (cache.size > MAX_CACHED_TILES) {
                cache.remove(cache.keys.first())
            }
            revision++
        }
    }

    private fun tileUrl(key: TileKey) =
        "https://tile.openstreetmap.org/${key.zoom}/${key.x}/${key.y}.png"

    companion object {
        /** OpenStreetMap's tile policy asks clients not to run many downloads in parallel. */
        private const val MAX_CONCURRENT_DOWNLOADS = 4

        /** Roughly 33MB of decoded ARGB tiles, which is several screenfuls of pan headroom. */
        private const val MAX_CACHED_TILES = 128

        private const val USER_AGENT =
            "BikeShareKMP/1.0 (+https://github.com/joreilly/BikeShare)"
    }
}

/**
 * The loader the maps in this app draw from. Provided by `BikeShareApp` so that the map shares
 * the application's configured Ktor client; where it is absent — Compose previews, for instance
 * — the map still renders its markers, just without a basemap under them.
 */
val LocalTileLoader = staticCompositionLocalOf<TileLoader?> { null }
