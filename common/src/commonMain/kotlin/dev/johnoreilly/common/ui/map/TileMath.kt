package dev.johnoreilly.common.ui.map

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sinh
import kotlin.math.tan

/** The Web Mercator projection is undefined at the poles; it is cut off here instead. */
const val MAX_LATITUDE = 85.05112878

// Web Mercator, normalised so the whole world is the unit square: (0, 0) is its north-west
// corner and (1, 1) its south-east one. This is the coordinate space MapCompose places markers
// and bounding boxes in, so it is all the projection maths the app needs.

fun lonToUnitX(longitude: Double): Double = (longitude + 180.0) / 360.0

fun latToUnitY(latitude: Double): Double {
    val radians = latitude.coerceIn(-MAX_LATITUDE, MAX_LATITUDE) * PI / 180.0
    return (1.0 - ln(tan(radians) + 1.0 / cos(radians)) / PI) / 2.0
}

fun unitXToLon(x: Double): Double = x * 360.0 - 180.0

fun unitYToLat(y: Double): Double = atan(sinh(PI * (1.0 - 2.0 * y))) * 180.0 / PI
