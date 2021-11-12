package com.surrus.bikeshare.glance

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.component.KoinComponent


class BikeShareAppWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
    override val glanceAppWidget: GlanceAppWidget = BikeShareAppWidget()
}