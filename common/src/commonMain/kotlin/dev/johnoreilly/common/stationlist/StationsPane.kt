package dev.johnoreilly.common.stationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.remote.Station
import dev.johnoreilly.common.ui.map.StationMap

/**
 * The stations of a network, shown either as the availability list or on a map, with a chip pair
 * to switch between them.
 *
 * Which view is showing is deliberately kept here rather than in the Circuit state: it is a
 * presentation preference with no bearing on what the presenter loads, and keeping it local lets
 * the full-screen station list and the tablet layout's detail pane each remember their own.
 */
@Composable
fun StationsPane(stations: List<Station>, modifier: Modifier = Modifier) {
    var showMap by rememberSaveable { mutableStateOf(false) }

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = !showMap,
                onClick = { showMap = false },
                label = { Text("List") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            )
            FilterChip(
                selected = showMap,
                onClick = { showMap = true },
                label = { Text("Map") },
                leadingIcon = { Icon(Icons.Default.Map, contentDescription = null) },
            )
        }

        if (showMap) {
            StationMap(stations, Modifier.weight(1f).fillMaxWidth())
        } else {
            StationListContent(stations)
        }
    }
}
