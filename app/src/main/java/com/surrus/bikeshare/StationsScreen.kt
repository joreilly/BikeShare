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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.common.remote.slots
import org.koin.androidx.compose.getViewModel

@Composable
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

