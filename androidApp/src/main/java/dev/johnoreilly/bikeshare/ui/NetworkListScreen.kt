@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.bikeshare.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.johnoreilly.bikeshare.ui.viewmodel.BikeShareViewModel
import dev.johnoreilly.bikeshare.ui.viewmodel.Country
import dev.johnoreilly.common.model.Network
import org.koin.androidx.compose.getViewModel

@Composable
fun NetworkListScreen(countryCode: String, networkSelected: (network: String) -> Unit,  popBack: () -> Unit) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val groupedNetworkListState = bikeShareViewModel.groupedNetworks.collectAsState(initial = emptyMap())

    // TODO refactor/clean up this
    var networkList: List<Network>? = null
    var country: Country? = null
    val countryKeys = groupedNetworkListState.value.filterKeys { it.code == countryCode }
    if (countryKeys.isNotEmpty()) {
        country = countryKeys.keys.toList()[0]
        networkList = groupedNetworkListState.value[country]?.sortedBy { it.city }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BikeShare - ${country?.displayName}") },
                navigationIcon = {
                    IconButton(onClick = { popBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) { paddingValues ->
            networkList?.let {
                LazyColumn(Modifier.padding(paddingValues)) {
                    items(networkList) { network ->
                        NetworkView(network, networkSelected)
                    }
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
    //Divider()
}