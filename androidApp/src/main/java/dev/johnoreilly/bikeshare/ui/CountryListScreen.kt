@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.bikeshare.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.viewmodel.CountriesViewModelShared
import dev.johnoreilly.common.viewmodel.Country
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import java.util.*


@Composable
fun CountryListScreen(countrySelected: (country: Country) -> Unit) {
    val viewModel = getViewModel<CountriesViewModelShared>()
    val countryList by viewModel.countryList.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("BikeShare - Countries") }) }) { paddingValues ->

        Column(Modifier.padding(paddingValues)) {
            Box(Modifier.weight(1f)) {
                val listState = rememberLazyListState()

                LazyColumn(state = listState) {
                    items(countryList) { country ->
                        CountryView(country, countrySelected)
                    }
                }

                val showButton by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex > 0
                    }
                }
                if (showButton) {
                    val coroutineScope = rememberCoroutineScope()
                    FloatingActionButton(
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

@SuppressLint("DiscouragedApi")
@Composable
fun CountryView(country: Country, countrySelected: (country: Country) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { countrySelected(country) })
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val flagResourceId = context.resources.getIdentifier("flag_${country.code.lowercase(Locale.getDefault())}", "drawable", context.packageName)
        if (flagResourceId != 0) {
            Image(painterResource(flagResourceId), modifier = Modifier.size(32.dp), contentDescription = country.displayName)
        }

        Spacer(modifier = Modifier.size(16.dp))
        Text(text = country.displayName, style = MaterialTheme.typography.bodyLarge)
    }
}

