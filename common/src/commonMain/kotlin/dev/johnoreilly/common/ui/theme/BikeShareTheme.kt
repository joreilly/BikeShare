package dev.johnoreilly.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Generated with material-color-utilities: tonal-spot neutrals and containers from the BikeShare
// green (#1E8E3E), primary from the full-chroma green (tone 45 in light so white text on it passes
// 4.5:1), and secondary from the blue (#1976D2) that marks empty docks next to the bike counts.

private val LightColors = lightColorScheme(
    primary = Color(0xFF007C30),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB8F0B8),
    onPrimaryContainer = Color(0xFF1E5027),
    inversePrimary = Color(0xFF73DC82),
    secondary = Color(0xFF005FAF),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD4E3FF),
    onSecondaryContainer = Color(0xFF004786),
    tertiary = Color(0xFF39656C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBDEAF2),
    onTertiaryContainer = Color(0xFF1F4D54),
    background = Color(0xFFF7FBF2),
    onBackground = Color(0xFF181D18),
    surface = Color(0xFFF7FBF2),
    onSurface = Color(0xFF181D18),
    surfaceVariant = Color(0xFFDEE5D9),
    onSurfaceVariant = Color(0xFF424940),
    surfaceTint = Color(0xFF007C30),
    inverseSurface = Color(0xFF2D322C),
    inverseOnSurface = Color(0xFFEEF2E9),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    outline = Color(0xFF727970),
    outlineVariant = Color(0xFFC1C9BE),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF7FBF2),
    surfaceContainer = Color(0xFFEBEFE6),
    surfaceContainerHigh = Color(0xFFE5E9E1),
    surfaceContainerHighest = Color(0xFFE0E4DB),
    surfaceContainerLow = Color(0xFFF1F5EC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD7DBD3),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF73DC82),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF1E5027),
    onPrimaryContainer = Color(0xFFB8F0B8),
    inversePrimary = Color(0xFF007C30),
    secondary = Color(0xFFA5C8FF),
    onSecondary = Color(0xFF00315F),
    secondaryContainer = Color(0xFF004786),
    onSecondaryContainer = Color(0xFFD4E3FF),
    tertiary = Color(0xFFA1CED6),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF1F4D54),
    onTertiaryContainer = Color(0xFFBDEAF2),
    background = Color(0xFF101510),
    onBackground = Color(0xFFE0E4DB),
    surface = Color(0xFF101510),
    onSurface = Color(0xFFE0E4DB),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC1C9BE),
    surfaceTint = Color(0xFF73DC82),
    inverseSurface = Color(0xFFE0E4DB),
    inverseOnSurface = Color(0xFF2D322C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF8C9389),
    outlineVariant = Color(0xFF424940),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF363A35),
    surfaceContainer = Color(0xFF1C211C),
    surfaceContainerHigh = Color(0xFF272B26),
    surfaceContainerHighest = Color(0xFF313630),
    surfaceContainerLow = Color(0xFF181D18),
    surfaceContainerLowest = Color(0xFF0B0F0B),
    surfaceDim = Color(0xFF101510),
)

/**
 * How well stocked a station is, used by the list's progress bar and bike icon and by the map
 * markers. The colours in each set stay apart under deuteranopia and protanopia (lightness differs,
 * not just hue), and all are at least 3:1 against their mode's surface except the light-mode amber
 * (#FFA000, about 2:1), kept for a true amber. Numbers are drawn in the normal text colour, so
 * these never carry text on their own.
 */
@Immutable
data class AvailabilityColors(val low: Color, val medium: Color, val high: Color)

private val LightAvailability = AvailabilityColors(
    low = Color(0xFF9B1B12),
    medium = Color(0xFFFFA000),
    high = Color(0xFF10A535),
)

private val DarkAvailability = AvailabilityColors(
    low = Color(0xFFF28B73),
    medium = Color(0xFFFFD43B),
    high = Color(0xFF168C25),
)

val LocalAvailabilityColors = staticCompositionLocalOf { LightAvailability }

/**
 * Colours the Material scheme doesn't have a role for.
 *
 * The app bar is the brand green in light mode. In dark mode `primary` is a light green, which is
 * glaring as a full-width bar and puts the light status-bar icons on a light background, so the
 * bar uses the dark primary container there instead.
 *
 * Cards are white in light mode; in dark mode they're a step above the page, since card shadows
 * don't show on a dark background.
 */
@Immutable
data class BikeShareColors(val appBar: Color, val onAppBar: Color, val card: Color)

private val LightBikeShareColors = BikeShareColors(
    appBar = LightColors.primary,
    onAppBar = LightColors.onPrimary,
    card = LightColors.surfaceContainerLowest,
)

private val DarkBikeShareColors = BikeShareColors(
    appBar = DarkColors.primaryContainer,
    onAppBar = DarkColors.onPrimaryContainer,
    card = DarkColors.surfaceContainerHigh,
)

val LocalBikeShareColors = staticCompositionLocalOf { LightBikeShareColors }

/** The one theme for every platform's Compose UI (Android, desktop and web). */
@Composable
fun BikeShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAvailabilityColors provides if (darkTheme) DarkAvailability else LightAvailability,
        LocalBikeShareColors provides if (darkTheme) DarkBikeShareColors else LightBikeShareColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content,
        )
    }
}
