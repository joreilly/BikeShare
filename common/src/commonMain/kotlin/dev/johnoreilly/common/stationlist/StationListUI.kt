@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.stationlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import bikeshare.common.generated.resources.Res
import bikeshare.common.generated.resources.ic_bike
import bikeshare.common.generated.resources.marker
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.johnoreilly.common.mapOptions
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.remote.freeBikes
import dev.johnoreilly.common.screens.StationListScreen
import dev.sargunv.maplibrecompose.compose.ClickResult
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.CircleLayer
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.compose.rememberStyleState
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.GestureOptions
import dev.sargunv.maplibrecompose.core.MapOptions
import dev.sargunv.maplibrecompose.core.OrnamentOptions
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import dev.sargunv.maplibrecompose.expressions.dsl.Feature.get
import dev.sargunv.maplibrecompose.expressions.dsl.asString
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.feature
import dev.sargunv.maplibrecompose.expressions.dsl.format
import dev.sargunv.maplibrecompose.expressions.dsl.image
import dev.sargunv.maplibrecompose.expressions.dsl.offset
import dev.sargunv.maplibrecompose.expressions.dsl.span
import io.github.dellisd.spatialk.geojson.Point
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import io.github.dellisd.spatialk.geojson.Position
import io.github.dellisd.spatialk.geojson.dsl.featureCollection
import org.jetbrains.compose.resources.ExperimentalResourceApi

val lowAvailabilityColor = Color(0xFFFF8C00)
val highAvailabilityColor = Color(0xFF008000)


@CircuitInject(StationListScreen::class, AppScope::class)
@Composable
fun StationListUI(state: StationListScreen.State, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val titles = listOf("List", "Map")
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(state.networkId) },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(StationListScreen.Event.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->

        Column(Modifier.padding(paddingValues)) {

            SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(text = title) }
                    )
                }
            }

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> StationListView(state.stationList)
                    1 -> MapView(state.stationList)
                }
            }
        }
    }
}


@Composable
fun StationView(station: Station) {

    Row(
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Image(
                painterResource(Res.drawable.ic_bike),
                colorFilter = ColorFilter.tint(if (station.freeBikes() < 2) lowAvailabilityColor else highAvailabilityColor),
                modifier = Modifier.size(32.dp), contentDescription = station.freeBikes().toString()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Column(modifier = Modifier.width(100.dp)) {
                        Text("Bikes")
                        Text(
                            station.freeBikes().toString(),
                            color = if (station.freeBikes() < 2) lowAvailabilityColor else highAvailabilityColor
                        )
                    }

                    Column {
                        Text("Stands")
                        Text(
                            station.empty_slots.toString(),
                            color = if (station.freeBikes() < 2) lowAvailabilityColor else highAvailabilityColor
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun StationListView(stationList: List<Station>) {
    LazyColumn {
        items(stationList.sortedBy { it.name }) { station ->
            StationView(station)
        }
    }
}


data class StyleInfo(val name: String, val uri: String, val isDark: Boolean)

@OptIn(ExperimentalResourceApi::class)
val ALL_STYLES =
    listOf(
        StyleInfo("Bright", "https://tiles.openfreemap.org/styles/bright", isDark = false),
//        StyleInfo("Liberty", "https://tiles.openfreemap.org/styles/liberty", isDark = false),
//        StyleInfo("Positron", "https://tiles.openfreemap.org/styles/positron", isDark = false),
//        StyleInfo("Fiord", "https://tiles.openfreemap.org/styles/fiord", isDark = true),
//        StyleInfo("Dark", "https://tiles.openfreemap.org/styles/dark", isDark = true),
//        StyleInfo("Colorful", Res.getUri("files/styles/colorful.json"), isDark = false),
//        StyleInfo("Eclipse", Res.getUri("files/styles/eclipse.json"), isDark = true),
//        StyleInfo("OSM Carto", Res.getUri("files/styles/osm-raster.json"), isDark = false),
    )

val DEFAULT_STYLE = ALL_STYLES[0].uri
//val MINIMAL_STYLE = ALL_STYLES[2].uri


fun Station.position() = Position(latitude = latitude, longitude = longitude)


@Composable
fun MapView(stationList: List<Station>) {
    val styleState = rememberStyleState()
    val marker = painterResource(Res.drawable.marker)

    val cameraPosition = stationList[0].position()
    val cameraState =
        rememberCameraState(firstPosition = CameraPosition(target = cameraPosition, zoom = 11.0))


    MaplibreMap(
        styleUri = "https://tiles.openfreemap.org/styles/bright",
        cameraState = cameraState,
        styleState = styleState,
        options = mapOptions
    ) {
        val bikeStations = rememberGeoJsonSource(
            id = "stations",
            data = GeoJsonData.Features(featureCollection {
                stationList.forEach { station ->
                    feature(Point(station.position()))
                }
            })
        )

        SymbolLayer(
            id = "stations",
            source = bikeStations,
            iconImage = image(marker)
        )
    }
}

