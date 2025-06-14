package dev.johnoreilly.common

import dev.sargunv.maplibrecompose.core.GestureOptions
import dev.sargunv.maplibrecompose.core.MapOptions
import java.util.Locale

actual fun getCountryName(countryCode: String): String {
    val locale = Locale("", countryCode)
    return locale.displayCountry
}


actual val mapOptions = MapOptions(
    gestureOptions =
        GestureOptions(
            isZoomEnabled = true,
            isScrollEnabled = true,
        )
)