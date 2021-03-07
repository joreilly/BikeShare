import androidx.compose.animation.animate
import androidx.compose.desktop.AppManager
import androidx.compose.desktop.ComposePanel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import javax.swing.SwingUtilities
import javax.swing.WindowConstants


data class Country(val code: String, val displayName: String)

fun main() {
    AppManager.setEvents(onAppStart = null, onAppExit = null, onWindowsEmpty = null)
    SwingComposeWindow()
}


var map: JXMapViewer? = null


fun SwingComposeWindow() = SwingUtilities.invokeLater {
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

    window.setSize(900, 600)
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
    val cityBikesApi = CityBikesApi

    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedNetwork by remember { mutableStateOf<Network?>(null) }

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

    LaunchedEffect(selectedNetwork) {
        selectedNetwork?.let {
            val city = GeoPosition(it.location.latitude, it.location.longitude)
            map?.addressLocation = city

            stationList = cityBikesApi.fetchBikeShareInfo(it.id).network.stations

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
            Box(Modifier.width(200.dp).fillMaxHeight().background(color = Color(0xff100c08))) {
                LazyColumnFor(items = countryList, itemContent = { country ->
                    Row(
                        Modifier.clickable(onClick = {
                            selectedCountry = country
                            networkList = groupedNetworkList[country]!!
                        }).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(country.displayName,
                            color = animate(if (country == selectedCountry) Color.White else Color.LightGray),
                            style = if (country == selectedCountry) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
                        )
                    }
                })
            }

            Spacer(modifier = Modifier.preferredWidth(1.dp).fillMaxHeight()
                    .background(color = MaterialTheme.colors.onSurface.copy(0.25f)))

            Box {
                LazyColumnFor(items = networkList, itemContent = { network ->
                    Row(
                        Modifier.clickable(onClick = {
                            selectedNetwork = network
                        }).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${network.name} (${network.location.city})",
                            color = animate(if (network == selectedNetwork) Color.Black else Color.Gray),
                            style = if (network == selectedNetwork) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
                        )
                    }
                })
            }

        }
    }
}