@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.networklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.screens.NetworkListScreen
import dev.johnoreilly.common.stationlist.StationListContent
import dev.johnoreilly.common.ui.AdaptiveLayout
import software.amazon.lastmile.kotlin.inject.anvil.AppScope


@CircuitInject(NetworkListScreen::class, AppScope::class)
@Composable
fun NetworkListUi(state: NetworkListScreen.State, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(state.countryName) },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(NetworkListScreen.Event.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        AdaptiveLayout(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            compactContent = {
                // Show only network list on narrow screens
                NetworkList(
                    networkList = state.networkList,
                    onNetworkSelected = { networkId ->
                        state.eventSink(NetworkListScreen.Event.NetworkClicked(networkId))
                    }
                )
            },
            expandedContent = {
                // Show network list and station list side by side on wider screens
                Row(modifier = Modifier.fillMaxSize()) {
                    // Network list (left side)
                    NetworkList(
                        modifier = Modifier.weight(0.4f),
                        networkList = state.networkList,
                        onNetworkSelected = { networkId ->
                            state.eventSink(NetworkListScreen.Event.SelectNetwork(networkId))
                        }
                    )
                    
                    // Station list (right side)
                    Box(
                        modifier = Modifier.weight(0.6f)
                    ) {
                        state.stationListState?.let { stationListState ->
                            // Show actual station list for the selected network
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stationListState.networkId,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                
                                if (stationListState.isLoadingStations) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                } else {
                                    StationListContent(stationListState.stationList)
                                }
                            }
                        } ?: run {
                            // Placeholder when no network is selected
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Select a network to view stations",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun NetworkList(
    networkList: List<Network>,
    onNetworkSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(networkList) { network ->
            NetworkView(network) {
                onNetworkSelected(network.id)
            }
        }
    }
}

@Composable
fun NetworkView(network: Network, networkSelected: (network: String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { networkSelected(network.id) })
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(text = "${network.city} (${network.name})",
            style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}