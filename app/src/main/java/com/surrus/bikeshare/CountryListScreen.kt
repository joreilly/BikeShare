package com.surrus.bikeshare

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.bikeshare.ui.viewmodel.Country
import com.surrus.common.remote.freeBikes
import org.koin.androidx.compose.getViewModel


@Composable
fun CountryListScreen(countrySelected: (countryCode: String) -> Unit) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val groupedNetworkListState = bikeShareViewModel.groupedNetworks.collectAsState(initial = emptyMap())

    val countryCodeLIst = groupedNetworkListState.value.keys.toList().sortedBy { it.displayName }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BikeShare - Countries") })
        },
        bodyContent = {
            LazyColumn {
                items(countryCodeLIst) { country ->
                    CountryView(country, countrySelected)
                }
            }
        }
    )
}

@Composable
fun CountryView(country: Country, countrySelected: (countryCode: String) -> Unit) {
    val context = AmbientContext.current

    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = { countrySelected(country.code) })
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val flagResourceId = context.resources.getIdentifier("flag_${country.code}", "drawable", context.getPackageName())
        if (flagResourceId != 0) {
            Image(vectorResource(flagResourceId), modifier = Modifier.preferredSize(32.dp), contentDescription = null)
        }

        Spacer(modifier = Modifier.preferredSize(16.dp))
        Text(text = country.displayName, style = MaterialTheme.typography.h6)
    }
    Divider()
}

