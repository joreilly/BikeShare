import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("BikeShare", canvasElementId = "bikeShareCanvas") {
        Column {
            Text(
                text ="Bike Share (powered by CityBikes)",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall)

            BikeShareView()
        }
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

data class Country(val code: String, val displayName: String)


@Composable
fun BikeShareView()  {
    val cityBikesApi = remember { CityBikesApi() }

    var fullNetworkList by remember { mutableStateOf(emptyList<NetworkDTO>()) }
    var countryList by remember { mutableStateOf(emptyList<Country>()) }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }

    var networkList by remember { mutableStateOf(emptyList<NetworkDTO>()) }
    var selectedNetwork by remember { mutableStateOf<NetworkDTO?>(null) }

    var stationList by remember { mutableStateOf(emptyList<Station>()) }

    LaunchedEffect(Unit) {
        fullNetworkList = cityBikesApi.fetchNetworkList().networks

        countryList = fullNetworkList.groupBy { it.location.country }.keys.toList().map { countryCode ->
            Country(countryCode, getCountryName(countryCode))
        }.sortedBy { it.displayName }
    }


    LaunchedEffect(selectedCountry) {
        networkList = fullNetworkList
            .filter { it.location.country == selectedCountry?.code }
            .sortedBy { it.location.city }
    }

    LaunchedEffect(selectedNetwork) {
        selectedNetwork?.let {
            stationList = cityBikesApi.fetchBikeShareInfo(it.id).network.stations
        }
    }

    Row(Modifier.fillMaxHeight()) {
        Box(Modifier.width(250.dp).background(color = Color.LightGray)) {
            LazyColumn {
                items(countryList) { country ->
                    Row(
                        Modifier.clickable(onClick = {
                            selectedCountry = country
                        }).fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            country.displayName,
                            color = Color.Black,
                            style = if (country == selectedCountry) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.onSurface.copy(0.25f)))

        Box(Modifier.width(400.dp)) {
            LazyColumn {
                items(networkList) { network ->
                    Row(
                        Modifier.clickable(onClick = {
                            selectedNetwork = network
                        }).fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${network.location.city} (${network.name})",
                            color = Color.Black,
                            style = if (network == selectedNetwork) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.onSurface.copy(0.25f)))

        StationsScreen(stationList)
    }
}


@JsFun(
    """ (countryCode) => {
        const regionNames = new Intl.DisplayNames(['en'], {type: 'region'});        
        return regionNames.of(countryCode)
    }
"""
)
external fun getCountryName(countryCode: String): String

