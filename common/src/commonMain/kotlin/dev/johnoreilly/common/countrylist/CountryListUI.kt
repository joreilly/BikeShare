@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.countrylist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bikeshare.common.generated.resources.Res
import bikeshare.common.generated.resources.allDrawableResources
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.johnoreilly.common.screens.CountryListScreen
import dev.johnoreilly.common.viewmodel.Country
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import software.amazon.lastmile.kotlin.inject.anvil.AppScope


@CircuitInject(CountryListScreen::class, AppScope::class)
@Composable
fun CountryListUi(state: CountryListScreen.State, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier, 
        topBar = { 
            TopAppBar(
                title = { Text("Choose a Country", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) 
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { countrySelected(country) }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val flagDrawable = Res.allDrawableResources["flag_${country.code.lowercase()}"]
            if (flagDrawable != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Image(
                        painterResource(flagDrawable),
                        modifier = Modifier.size(40.dp),
                        contentDescription = country.displayName
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = country.displayName, 
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}