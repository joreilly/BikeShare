import androidx.compose.desktop.AppManager
import androidx.compose.desktop.ComposePanel
import androidx.compose.desktop.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import com.surrus.common.remote.Station
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.*
import java.awt.GridLayout
import java.util.*
import javax.swing.JFrame
import javax.swing.WindowConstants


data class Country(val code: String, val displayName: String)

fun main() {
    AppManager.setEvents(onAppStart = null, onAppExit = null, onWindowsEmpty = null)
    SwingComposeWindow()
}


var map: JXMapViewer? = null


fun SwingComposeWindow() {
    val window = JFrame()
    window.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    window.title = "BikeShare"

    map = createMap()

    val composePanel = ComposePanel()
    window.layout = GridLayout(1, 2)
    window.add(composePanel)
    window.add(map)

    composePanel.setContent {
        BikeShareView()
    }

    window.setSize(800, 600)
    window.isVisible = true
}

fun createMap() : JXMapViewer {
    val mapViewer = JXMapViewer()

    val info: TileFactoryInfo = OSMTileFactoryInfo()
    val tileFactory = DefaultTileFactory(info)
    mapViewer.tileFactory = tileFactory

    mapViewer.zoom = 5
    return mapViewer
}

@Composable
fun BikeShareView()  {
    val cityBikesApi = CityBikesApi()
    var networkId by remember { mutableStateOf("") }
    var networkList by remember { mutableStateOf(emptyList<Network>()) }
    var stationList by remember { mutableStateOf(emptyList<Station>()) }
    var groupedNetworkList: Map<Country, List<Network>> by remember { mutableStateOf(emptyMap()) }
    var countryList by remember { mutableStateOf(emptyList<Country>()) }


    LaunchedEffect(true) {
        val allNetworks = cityBikesApi.fetchNetworkList().networks
        val groupedNetworkListByCountryCode = allNetworks.groupBy { it.location.country }
        groupedNetworkList = groupedNetworkListByCountryCode.mapKeys {
            val countryCode = it.key.toLowerCase()
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            Country(countryCode, countryName)
        }

        countryList = groupedNetworkList.keys.toList().sortedBy { it.displayName }
        println(countryList)
    }

    LaunchedEffect(networkId) {
        if (networkId.isNotEmpty()) {
            stationList = cityBikesApi.fetchBikeShareInfo(networkId).network.stations

            val wpp = WaypointPainter<Waypoint>()
            val wpSet = mutableSetOf<Waypoint>()
            stationList.forEach {
                val wp = Waypoint() {
                    GeoPosition(it.latitude, it.longitude)
                }
                wpSet.add(wp)
            }

            wpp.setWaypoints(wpSet)
            map?.overlayPainter = wpp
        }
    }

    MaterialTheme {
        Row {
            Box(Modifier.width(200.dp).fillMaxHeight()) {
                LazyColumnFor(items = countryList, itemContent = { country ->
                    Row(
                        Modifier.clickable(onClick = {
                            networkList = groupedNetworkList[country]!!
                        }).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(country.displayName)
                    }
                })
            }

            Spacer(modifier = Modifier.preferredWidth(1.dp).fillMaxHeight()
                    .background(color = MaterialTheme.colors.onSurface.copy(0.25f)))

            Box {
                LazyColumnFor(items = networkList, itemContent = { network ->
                    Row(
                        Modifier.clickable(onClick = {
                            networkId = network.id
                            val city = GeoPosition(network.location.latitude, network.location.longitude)
                            map?.addressLocation = city
                        }).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${network.name} (${network.location.city})")
                    }
                })
            }

        }
    }
}