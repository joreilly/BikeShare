@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeshare.common.generated.resources.Res
import bikeshare.common.generated.resources.ic_bike
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.remote.freeBikes
import dev.johnoreilly.common.screens.StationListScreen
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.painterResource

val lowAvailabilityColor = Color(0xFFFF8C00)
val highAvailabilityColor = Color(0xFF008000)


@Inject
@Composable
fun StationListUI(state: StationListScreen.State, modifier: Modifier = Modifier) {
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
        LazyColumn(Modifier.padding(paddingValues)) {
            items(state.stationList.sortedBy { it.name }) { station ->
                StationView(station)
            }
        }
    }
}


@Composable
fun StationView(station: Station) {

    Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {

        Column {
            Image(
                painterResource(Res.drawable.ic_bike),
                colorFilter = ColorFilter.tint(if (station.freeBikes() < 2) lowAvailabilityColor else highAvailabilityColor),
                modifier = Modifier.size(32.dp), contentDescription = station.freeBikes().toString())
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1.0f)) {
            Text(text = station.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)

            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Column(modifier = Modifier.width(100.dp)) {
                        Text("Bikes")
                        Text(station.freeBikes().toString(),
                            color = if (station.freeBikes() < 2) lowAvailabilityColor else highAvailabilityColor)
                    }

                    Column {
                        Text("Stands")
                        Text(station.empty_slots.toString(),
                            color = if (station.freeBikes() < 2) lowAvailabilityColor else highAvailabilityColor)
                    }
                }
            }
        }

    }
}


