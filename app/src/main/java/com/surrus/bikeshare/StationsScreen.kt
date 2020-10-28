package com.surrus.bikeshare

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.common.remote.slots
import org.koin.androidx.compose.getViewModel

@Composable
fun StationsScreen(navController: NavController, networkId: String) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val stationsState = bikeShareViewModel.stations.collectAsState()

    bikeShareViewModel.setCity(networkId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BikeShare - $networkId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack) }
                }
            )
        },
        bodyContent = { innerPadding ->
            LazyColumnFor(
                contentPadding = innerPadding,
                items = stationsState.value) { station ->
                StationView(station)
            }
        }
    )
}


@Composable
fun StationView(station: Station) {
    Card(elevation = 12.dp, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Image(asset = vectorResource(R.drawable.ic_bike),
                colorFilter = ColorFilter.tint(if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor),
                modifier = Modifier.preferredSize(32.dp))

            Spacer(modifier = Modifier.preferredSize(16.dp))

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

