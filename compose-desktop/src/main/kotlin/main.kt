
import androidx.compose.desktop.AppManager
import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surrus.common.remote.CityBikesApi
import com.surrus.common.remote.Network
import kotlinx.coroutines.flow.map
import java.util.*
import org.jxmapviewer.JXMapViewer
import javax.swing.JFrame
import org.jxmapviewer.viewer.GeoPosition

import org.jxmapviewer.viewer.DefaultTileFactory

import org.jxmapviewer.OSMTileFactoryInfo

import org.jxmapviewer.viewer.TileFactoryInfo
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities.invokeLater


data class Country(val code: String, val displayName: String)



fun main() = Window {
    val cityBikesApi = CityBikesApi()
    var networkList by remember { mutableStateOf(emptyList<Network>()) }

    var groupedNetworkList: Map<Country,List<Network>> by remember { mutableStateOf(emptyMap()) }
    var countryList by remember { mutableStateOf(emptyList<Country>()) }


    launchInComposition {

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


    val mapViewer = JXMapViewer()

    // Create a TileFactoryInfo for OpenStreetMap
    val info: TileFactoryInfo = OSMTileFactoryInfo()
    val tileFactory = DefaultTileFactory(info)
    mapViewer.tileFactory = tileFactory

    // Set the focus
    val frankfurt = GeoPosition(50.11, 8.68)

    mapViewer.zoom = 7
    mapViewer.addressLocation = frankfurt


    val frame = JFrame("JXMapviewer2 Example 1")
    frame.contentPane.add(mapViewer)
    frame.setSize(800, 600)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true

    MaterialTheme {
        Row {
            LazyColumnFor(items = countryList, itemContent = { country ->
                Row(Modifier.clickable(onClick = {
                        networkList = groupedNetworkList[country]!!
                    })
                    + Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(country.displayName)
                }
            })
            LazyColumnFor(items = networkList, itemContent = { network ->
                Row(Modifier.clickable(onClick = {
                })  + Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${network.name} (${network.location.city})")
                }

            })

        }
    }
}