@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.networklist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
                title = { Text(state.countryName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(NetworkListScreen.Event.BackClicked) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        AdaptiveLayout(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            compactContent = {
                // Show only network list on narrow screens
                NetworkList(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                    Surface(
                        modifier = Modifier.weight(0.4f),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NetworkList(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            networkList = state.networkList,
                            onNetworkSelected = { networkId ->
                                state.eventSink(NetworkListScreen.Event.SelectNetwork(networkId))
                            }
                        )
                    }
                    
                    Divider(modifier = Modifier.width(1.dp).fillMaxSize())
                    
                    // Station list (right side)
                    Surface(
                        modifier = Modifier.weight(0.6f),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        state.stationListState?.let { stationListState ->
                            // Show actual station list for the selected network
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            ) {
                                Text(
                                    text = stationListState.networkId,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                if (stationListState.isLoadingStations) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationCity,
                                        contentDescription = null,
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        "Select a network to view stations",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
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
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(networkList) { network ->
            NetworkView(network) {
                onNetworkSelected(network.id)
            }
        }
    }
}

@Composable
fun NetworkView(network: Network, networkSelected: (network: String) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColorAnimation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { 
                    networkSelected(network.id)
                    isPressed = !isPressed
                }
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = network.city,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = network.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}