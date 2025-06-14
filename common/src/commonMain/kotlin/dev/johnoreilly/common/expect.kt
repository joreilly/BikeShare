package dev.johnoreilly.common

import dev.sargunv.maplibrecompose.core.MapOptions

expect fun getCountryName(countryCode: String): String

expect val mapOptions: MapOptions