package com.surrus.bikeshare.glance

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.surrus.common.remote.CityBikesApi
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BikeShareAppWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
    private val cityBikesApi: CityBikesApi by inject()
    private val coroutineScope = MainScope()

    private val bikeShareWidget = BikeShareAppWidget()
    override val glanceAppWidget: GlanceAppWidget = bikeShareWidget

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        coroutineScope.launch {
            val stations = cityBikesApi.fetchBikeShareInfo("galway").network.stations

            bikeShareWidget.station = stations[0] // use first station for now
            bikeShareWidget.glanceId?.let {
                bikeShareWidget.update(context, it)
            }
        }
    }
}