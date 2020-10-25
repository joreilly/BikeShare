package com.surrus.bikeshare

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.AmbientNavController
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Network
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.getViewModel

@Composable
fun NetworkListScreen(countryCode: String, networkSelected: (network: String) -> Unit) {
    val navController = AmbientNavController.current
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()

    val countryKeys = bikeShareViewModel.groupedNetworks.value.filterKeys { it.code == countryCode }
    val country = countryKeys.keys.toList()[0]
    val networkList = bikeShareViewModel.groupedNetworks.value[country]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BikeShare - ${country.displayName}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack) }
                }
            )
        },
        bodyContent = {
            networkList?.let {
                LazyColumnFor(items = networkList) { network ->
                    NetworkView(network, networkSelected)
                }
            }
        }
    )
}

@Composable
fun NetworkView(network: Network, networkSelected: (network: String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth() + Modifier.clickable(onClick = { networkSelected(network.id) })
        + Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(text = network.name, style = MaterialTheme.typography.h6)
        Text(text = " (${network.location.city})", style = MaterialTheme.typography.h6)
    }
    Divider()
}