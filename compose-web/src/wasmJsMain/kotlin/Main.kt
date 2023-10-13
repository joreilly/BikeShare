import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("BikeShare", canvasElementId = "bikeShareCanvas") {
        val cityBikesApi = remember { CityBikesApi() }

        val stationList by pollNetworkUpdates("velib", cityBikesApi)
            .collectAsState(emptyList())
        StationsScreen(stationList)
    }
}

const val POLL_INTERVAL = 30000L

fun pollNetworkUpdates(network: String, cityBikesApi: CityBikesApi): Flow<List<Station>> = flow {
    while (true) {
        println("pollNetworkUpdates, network = $network")
        val stations = cityBikesApi.fetchBikeShareInfo(network)
        emit(stations.network.stations)
        delay(POLL_INTERVAL)
    }
}
