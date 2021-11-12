package com.surrus.bikeshare.glance

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.bikeshare.R
import com.surrus.common.remote.slots

class BikeShareAppWidget(val station: Station? = null) : GlanceAppWidget() {

    @Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize().background(Color.White).padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            station?.let { station ->
                Text(text = station.name, style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold))
                Spacer(modifier = GlanceModifier.size(8.dp))

                val bikeDrawable = if (station.freeBikes() < 5)
                    R.drawable.ic_bike_orange
                else
                    R.drawable.ic_bike_green
                Image(ImageProvider(bikeDrawable),
                    modifier = GlanceModifier.size(32.dp),
                    contentDescription = "${station.freeBikes()}")

                Spacer(modifier = GlanceModifier.size(8.dp))
                Row {
                    Text("Free:", modifier = GlanceModifier.width(80.dp))
                    Text("${station.freeBikes()}")
                }
                Row {
                    Text("Total:", modifier = GlanceModifier.width(80.dp))
                    Text("${station.slots()}")
                }
            }
        }
    }
}

