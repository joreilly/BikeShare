package com.surrus.bikeshare

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.bikeshare.ui.viewmodel.Country
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@Composable
fun CountryListScreen(countrySelected: (countryCode: String) -> Unit) {
    val bikeShareViewModel = getViewModel<BikeShareViewModel>()
    val groupedNetworkListState = bikeShareViewModel.groupedNetworks.collectAsState(initial = emptyMap())

    val countryCodeList = groupedNetworkListState.value.keys.toList().sortedBy { it.displayName }

    Scaffold(topBar = { TopAppBar(title = { Text("BikeShare - Countries") }) }) {

        Column {
            Box(Modifier.weight(1f)) {
                val listState = rememberLazyListState()
                CountryList(countryCodeList, listState, countrySelected)

                val showButton by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex > 0
                    }
                }
                if (showButton) {
                    val coroutineScope = rememberCoroutineScope()
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 16.dp, end = 16.dp),
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Scroll To Top")
                    }
                }
            }
        }
    }
}

@Composable
fun CountryList(
    countryCodeList: List<Country>,
    listState: LazyListState,
    countrySelected: (countryCode: String) -> Unit
) {
    LazyColumn(state = listState) {
        items(countryCodeList) { country ->
            CountryView(country, countrySelected)
        }
    }

}

@Composable
fun CountryView(country: Country, countrySelected: (countryCode: String) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { countrySelected(country.code) })
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val flagResourceId = context.resources.getIdentifier("flag_${country.code}", "drawable", context.getPackageName())
        if (flagResourceId != 0) {
            Image(painterResource(flagResourceId), modifier = Modifier.size(32.dp), contentDescription = country.displayName)
        }

        Spacer(modifier = Modifier.size(16.dp))
        Text(text = country.displayName, style = MaterialTheme.typography.h6)
    }
    Divider()
}

