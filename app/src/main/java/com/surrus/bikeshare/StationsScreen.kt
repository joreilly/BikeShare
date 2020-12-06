package com.surrus.bikeshare

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.common.remote.slots
import org.koin.androidx.compose.getViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


enum class Tabs(val title: String) {
    Map("Map"),
    List("List"),
}

class TabContent(val tab: Tabs, val content: @Composable (innerPadding: PaddingValues) -> Unit)


@Composable

/*
fun StationsScreen(networkId: String, popBack: (() -> Unit)?) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val stationsState = bikeShareViewModel.stations.collectAsState(emptyList())

    bikeShareViewModel.setCity(networkId)

    var navigationIcon:  @Composable() (() -> Unit)? = null
    if (popBack != null) { navigationIcon =  {
        IconButton(onClick = { popBack() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BikeShare - $networkId") }, navigationIcon = navigationIcon)
        }) { innerPadding ->
            LazyColumn(contentPadding = innerPadding) {
                items(stationsState.value) { station ->
                    StationView(station)
                }
*/

fun StationsScreen(networkId: String, latitude: String, longitude: String, popBack: (() -> Unit)?) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val stations by bikeShareViewModel.stations.collectAsState(emptyList())

//    val context = AmbientContext.current
//    val mapView = remember {
//        MapView(context)
////            .apply {
////            id = R.id.map
////        }
//    }

    val mapView = rememberOSMMapViewWithLifecycle()

    bikeShareViewModel.setCity(networkId)

    val mapTab = TabContent(Tabs.Map) { innerPadding ->
        //val map = MapView(AmbientContext.current)

        Column(Modifier.padding(innerPadding)) {

            AndroidView({ mapView }) { map ->
                map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                map.setMultiTouchControls(true)

                val mapController = map.controller
                mapController.setZoom(15.0)
                val startPoint = GeoPoint(latitude.toDouble(), longitude.toDouble())
                mapController.setCenter(startPoint)

                for (station in stations) {
                    val stationMarker = Marker(map)
                    stationMarker.position = GeoPoint(station.latitude, station.longitude)
                    stationMarker.title = station.name
                    map.overlays.add(stationMarker)
                }
            }

        }
    }

    val listTab = TabContent(Tabs.List) {
        LazyColumn {
            items(items = stations, itemContent = { station ->
                StationView(station)
            })
        }
    }

    val tabContent = listOf(listTab, mapTab)
    //val (currentTab, updateTab) = remember { tabContent.first().tab }
    var currentTab by remember { mutableStateOf(0) }
    //val selectedTabIndex = tabContent.indexOfFirst { it.tab == currentTab }


    var navigationIcon:  @Composable() (() -> Unit)? = null
    if (popBack != null) { navigationIcon =  {
        IconButton(onClick = { popBack() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BikeShare - $networkId") }, navigationIcon = navigationIcon)
        }) { innerPadding ->

//            Column(Modifier.padding(innerPadding)) {
//
//                AndroidView({ mapView }) { map ->
//
//                    map.setPadding(innerPadding.start.value.toInt(), innerPadding.top.value.toInt(), innerPadding.end.value.toInt(), innerPadding.bottom.value.toInt())
//                    map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
//                    map.setMultiTouchControls(true)
//
//                    val mapController = map.controller
//                    mapController.setZoom(15.0)
//                    val startPoint = GeoPoint(latitude.toDouble(), longitude.toDouble())
//                    mapController.setCenter(startPoint)
//
//                    for (station in stations) {
//                        val stationMarker = Marker(map)
//                        stationMarker.position = GeoPoint(station.latitude, station.longitude)
//                        stationMarker.title = station.name
//                        map.overlays.add(stationMarker)
//                    }
//                }
//
//            }


            Column {
                TabRow(
                    selectedTabIndex = currentTab
                ) {
                    tabContent.forEachIndexed { index, tabContent ->
                        Tab(
                            text = { Text(tabContent.tab.title) },
                            selected = currentTab == index,
                            onClick = { currentTab = index }
                        )
                    }
                }


                Column(Modifier.padding(innerPadding)) {

                    //mapView.setPadding(innerPadding.start.value.toInt(), 80, innerPadding.end.value.toInt(), innerPadding.bottom.value.toInt())

                    AndroidView({ mapView }) { map ->

                        //map.setPadding(innerPadding.start.value.toInt(), 80, innerPadding.end.value.toInt(), innerPadding.bottom.value.toInt())
                        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                        map.setMultiTouchControls(true)

                        val mapController = map.controller
                        mapController.setZoom(15.0)
                        val startPoint = GeoPoint(latitude.toDouble(), longitude.toDouble())
                        mapController.setCenter(startPoint)

                        for (station in stations) {
                            val stationMarker = Marker(map)
                            stationMarker.position = GeoPoint(station.latitude, station.longitude)
                            stationMarker.title = station.name
                            map.overlays.add(stationMarker)
                        }
                    }

                }


//                Box(modifier = Modifier.padding(innerPadding).weight(1f)) {
//                    // display the current tab content which is a @Composable () -> Unit
//                    tabContent[selectedTabIndex].content(innerPadding)
//                }
            }
        }
}


@Composable
fun StationView(station: Station) {
    Card(elevation = 12.dp, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Image(painterResource(R.drawable.ic_bike),
                colorFilter = ColorFilter.tint(if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor),
                modifier = Modifier.size(32.dp), contentDescription = station.freeBikes().toString())

            Spacer(modifier = Modifier.size(16.dp))

            Column {
                Text(text = station.name, style = MaterialTheme.typography.h6)

                val textStyle = MaterialTheme.typography.body2
                Row {
                    Text("Free:", style = textStyle, textAlign = TextAlign.Justify, modifier = Modifier.width(48.dp))
                    Text(text = station.freeBikes().toString(), style = textStyle)
                }
                Row {
                    Text("Slots:", style = textStyle, textAlign = TextAlign.Justify, modifier = Modifier.width(48.dp), )
                    Text(text = station.slots().toString(), style = textStyle)
                }
            }
        }
    }
}


@Composable
fun rememberOSMMapViewWithLifecycle(): org.osmdroid.views.MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
//            .apply {
//            id = R.id.map
//        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberOSMMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
//    onCommit(lifecycle) {
//        lifecycle.addObserver(lifecycleObserver)
//        onDispose {
//            lifecycle.removeObserver(lifecycleObserver)
//        }
//    }

    return mapView
}

@Composable
fun rememberOSMMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                //Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                //Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                //Lifecycle.Event.ON_STOP -> mapView.onStop()
                //Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                //else -> throw IllegalStateException()
            }
        }
    }
