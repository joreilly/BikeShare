package dev.johnoreilly.bikeshare.glance

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.johnoreilly.common.remote.freeBikes
import dev.johnoreilly.common.remote.slots
import dev.johnoreilly.bikeshare.R
import dev.johnoreilly.bikeshare.ui.theme.highAvailabilityColor
import dev.johnoreilly.bikeshare.ui.theme.lowAvailabilityColor
import dev.johnoreilly.common.repository.CityBikesRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BikeShareAppWidget : GlanceAppWidget(), KoinComponent {
    private val cityBikesRepository: CityBikesRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // token bike station for now until we have way to configure
        val station = cityBikesRepository.fetchBikeShareInfo("galway")[0]

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize().background(Color.White).padding(8.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Row(
                        GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Text(
                            text = station.name,
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = GlanceModifier.size(8.dp))

                    Image(
                        ImageProvider(R.drawable.ic_bike),
                        colorFilter = ColorFilter.tint(
                            if (station.freeBikes() < 5) ColorProvider(lowAvailabilityColor)
                            else ColorProvider(highAvailabilityColor)
                        ),
                        modifier = GlanceModifier.size(32.dp),
                        contentDescription = station.freeBikes().toString()
                    )

                    Spacer(modifier = GlanceModifier.size(8.dp))
                    Row {
                        Text("Free:", modifier = GlanceModifier.width(80.dp))
                        Text(station.freeBikes().toString())
                    }
                    Row {
                        Text("Total:", modifier = GlanceModifier.width(80.dp))
                        Text(station.slots().toString())
                    }
                }
            }
        }
    }
}

