@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.getCountryName
import dev.johnoreilly.common.model.Network
import dev.johnoreilly.common.viewmodel.NetworksViewModelShared
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias NetworkListScreen = @Composable (String, (network: String) -> Unit, () -> Unit) -> Unit

@Inject
@Composable
fun NetworkListScreen(viewModel: NetworksViewModelShared, @Assisted countryCode: String, @Assisted networkSelected: (network: String) -> Unit, @Assisted popBack: () -> Unit) {
    val networkList = viewModel.networkList.collectAsState()
    val countryName = remember { getCountryName(countryCode) }

    LaunchedEffect(countryCode) {
        viewModel.setCountryCode(countryCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(countryName) },
                navigationIcon = {
                    IconButton(onClick = { popBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) { paddingValues ->
            LazyColumn(Modifier.padding(paddingValues)) {
                items(networkList.value) { network ->
                    NetworkView(network, networkSelected)
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