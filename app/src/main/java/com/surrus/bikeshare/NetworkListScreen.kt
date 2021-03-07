package com.surrus.bikeshare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.bikeshare.ui.viewmodel.Country
import com.surrus.common.remote.Network
import org.koin.androidx.compose.getViewModel

@Composable
fun NetworkListScreen(countryCode: String, networkSelected: (network: String) -> Unit,  popBack: () -> Unit) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val groupedNetworkListState = bikeShareViewModel.groupedNetworks.collectAsState(initial = emptyMap())

    var networkList: List<Network>? = null
    var country: Country? = null
    val countryKeys = groupedNetworkListState.value.filterKeys { it.code == countryCode }
    if (countryKeys.isNotEmpty()) {
        country = countryKeys.keys.toList()[0]
        networkList = groupedNetworkListState.value[country]
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
        }) {
            networkList?.let {
                LazyColumn {
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

        Text(text = network.name, style = MaterialTheme.typography.h6)
        Text(text = " (${network.location.city})", style = MaterialTheme.typography.h6)
    }
    Divider()
}