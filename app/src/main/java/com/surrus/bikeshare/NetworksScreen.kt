package com.surrus.bikeshare

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Network
import org.koin.androidx.compose.getViewModel

@Composable
fun NetworksScreen() {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val networksState = bikeShareViewModel.networks.collectAsState()

    LazyColumnFor(items = networksState.value) { network ->
        NetworkView(network)
    }
}

@Composable
fun NetworkView(network: Network) {
    Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(text = network.name, style = MaterialTheme.typography.body1)
        Text(text = " (${network.location.city})", style = MaterialTheme.typography.body1)
    }
    Divider()
}