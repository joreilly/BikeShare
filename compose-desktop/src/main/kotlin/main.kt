import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.remote.CityBikesApi
import dev.johnoreilly.common.remote.NetworkDTO
import dev.johnoreilly.common.remote.Station
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.*
import java.awt.BorderLayout
import java.util.*
import javax.swing.JPanel


data class Country(val code: String, val displayName: String)

fun main() = singleWindowApplication(
    title = "BikeShare",
    state = WindowState(size = DpSize(1000.dp, 600.dp))
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.weight(1f)) {
            BikeShareView()
        }

        Column(Modifier.weight(1f)) {
            SwingPanel(
                background = Color.White,
                modifier = Modifier.fillMaxSize(),
                factory = {
                    map = createMap()

                    JPanel().apply {
                        layout = BorderLayout()
                        add(map)
                    }
                }
            )
        }
    }
}



var map: JXMapViewer? = null

fun createMap() : JXMapViewer {
    val mapViewer = JXMapViewer()

    val info: TileFactoryInfo = OSMTileFactoryInfo()
    val tileFactory = DefaultTileFactory(info)
    mapViewer.tileFactory = tileFactory

    mapViewer.zoom = 5
    return mapViewer
}

private val koin = initKoin().koin

@Composable
fun BikeShareView()  {

    val cityBikesApi = koin.get<CityBikesApi>()

    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedNetwork by remember { mutableStateOf<NetworkDTO?>(null) }
    var networkList by remember { mutableStateOf(emptyList<NetworkDTO>()) }

    var stationList by remember { mutableStateOf(emptyList<Station>()) }
    var groupedNetworkList: Map<Country, List<NetworkDTO>> by remember { mutableStateOf(emptyMap()) }
    var countryList by remember { mutableStateOf(emptyList<Country>()) }


    LaunchedEffect(true) {
        val allNetworks = cityBikesApi.fetchNetworkList().networks
        val groupedNetworkListByCountryCode = allNetworks.groupBy { it.location.country }
        groupedNetworkList = groupedNetworkListByCountryCode.mapKeys {
            val countryCode = it.key.lowercase(Locale.getDefault())
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            Country(countryCode, countryName)
        }


        countryList = groupedNetworkList.keys.toList().sortedBy { it.displayName }
        selectedCountry = countryList.first()
        groupedNetworkList[selectedCountry]?.let {
            networkList = it
            selectedNetwork = networkList.first()
        }
    }

    LaunchedEffect(selectedNetwork) {
        selectedNetwork?.let {
            val city = GeoPosition(it.location.latitude, it.location.longitude)
            map?.addressLocation = city

            stationList = cityBikesApi.fetchBikeShareInfo(it.id).network.stations

            val wpp = WaypointPainter<Waypoint>()
            val wpSet = mutableSetOf<Waypoint>()
            stationList.forEach {
                val wp = Waypoint {
                    GeoPosition(it.latitude, it.longitude)
                }
                wpSet.add(wp)
            }

            wpp.waypoints = wpSet
            map?.overlayPainter = wpp
        }
    }

    MaterialTheme {
        Row {
            Box(Modifier.width(200.dp).fillMaxHeight().background(color = Color.DarkGray)) {

                LazyColumn {
                    items(countryList) { country ->
                        Row(
                            Modifier.clickable(onClick = {
                                selectedCountry = country
                                networkList = groupedNetworkList[country]!!
                            }).padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                country.displayName,
                                color = Color.White,
                                style = if (country == selectedCountry) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight()
                    .background(color = MaterialTheme.colors.onSurface.copy(0.25f)))

            Box {
                LazyColumn {
                    items(networkList) { network ->
                        Row(
                            Modifier.clickable(onClick = {
                                selectedNetwork = network
                            }).padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${network.name} (${network.location.city})",
                                color = Color.Black,
                                style = if (network == selectedNetwork) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
    }
}