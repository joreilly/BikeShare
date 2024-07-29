@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bikeshare.common.generated.resources.Res
import bikeshare.common.generated.resources.allDrawableResources
import dev.johnoreilly.common.screens.CountryListScreen
import dev.johnoreilly.common.viewmodel.Country
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@Composable
fun CountryListUi(state: CountryListScreen.State, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier, topBar = { TopAppBar(title = { Text("Countries") }) }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(state.countryList) { country ->
                CountryView(country) {
                    state.eventSink(CountryListScreen.Event.CountryClicked(country.code))
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun CountryView(country: Country, countrySelected: (country: Country) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { countrySelected(country) })
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val flagDrawable = Res.allDrawableResources["flag_${country.code.lowercase()}"]
        if (flagDrawable != null) {
            Image(
                painterResource(flagDrawable),
                modifier = Modifier.size(32.dp),
                contentDescription = country.displayName
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
        Text(text = country.displayName, style = MaterialTheme.typography.bodyLarge)
    }
}