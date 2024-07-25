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
import dev.johnoreilly.common.di.DesktopApplicationComponent
import dev.johnoreilly.common.di.SharedApplicationComponent
import dev.johnoreilly.common.di.create
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.viewmodel.Country
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.*
import java.awt.BorderLayout
import javax.swing.JPanel


fun main() {
    // see https://github.com/JetBrains/compose-multiplatform-core/pull/601
    System.setProperty("compose.swing.render.on.graphics", "true")

    val applicationComponent = DesktopApplicationComponent.create()


    return singleWindowApplication(
        title = "BikeShare",
        state = WindowState(size = DpSize(1000.dp, 600.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(Modifier.weight(1f)) {
                BikeShareView(applicationComponent)
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


@Composable
fun BikeShareView(applicationComponent: SharedApplicationComponent) {
    val countriesViewModel = remember { applicationComponent.countriesViewModel }
    val countryList by countriesViewModel.countryList.collectAsState()
    var selectedCountry by remember { mutableStateOf<Country?>(null) }

    val networksViewModel= remember { applicationComponent.networksViewModel }
    val networkList by networksViewModel.networkList.collectAsState()
    var selectedNetwork by remember { mutableStateOf<Network?>(null) }

    val stationsViewModel= remember { applicationComponent.stationsViewModel }
    val stations by stationsViewModel.stations.collectAsState()

    // probably cleaner way of doing this....
    LaunchedEffect(networkList) {
        selectedNetwork = networkList.firstOrNull()
        selectedNetwork?.let {
            stationsViewModel.setNetwork(it.id)
        }
    }

    LaunchedEffect(countryList) {
        selectedCountry = countryList.firstOrNull()
        selectedCountry?.let {
            networksViewModel.setCountryCode(it.code)
        }
    }

    LaunchedEffect(stations) {
        selectedNetwork?.let {
            val city = GeoPosition(it.latitude, it.longitude)
            map?.addressLocation = city

            val wpp = WaypointPainter<Waypoint>()
            val wpSet = mutableSetOf<Waypoint>()
            stations.forEach {
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
                                networksViewModel.setCountryCode(country.code)
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
                                stationsViewModel.setNetwork(network.id)
                            }).padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${network.name} (${network.city})",
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