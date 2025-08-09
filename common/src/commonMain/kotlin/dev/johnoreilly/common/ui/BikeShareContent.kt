package dev.johnoreilly.common.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.johnoreilly.common.screens.CountryListScreen
import me.tatarka.inject.annotations.Inject

// BikeShare theme colors
private val BikeGreen = Color(0xFF1E8E3E)
private val BikeGreenLight = Color(0xFF7CB342)
private val BikeGreenDark = Color(0xFF005D1B)
private val BikeBlue = Color(0xFF1976D2)
private val BikeBlueDark = Color(0xFF0D47A1)

private val LightColorScheme = lightColorScheme(
    primary = BikeGreen,
    secondary = BikeBlue,
    tertiary = BikeGreenLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BikeGreenLight,
    secondary = BikeBlue,
    tertiary = BikeGreenDark
)

typealias BikeShareApp = @Composable () -> Unit

@Inject
@Composable
fun BikeShareApp(circuit: Circuit) {
    // Determine if we should use dark theme
    val darkTheme = isSystemInDarkTheme()
    
    // Use the appropriate color scheme
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        val backStack = rememberSaveableBackStack(root = CountryListScreen)
        val navigator = rememberCircuitNavigator(backStack) {}

        CircuitCompositionLocals(circuit) {
            NavigableCircuitContent(navigator = navigator, backStack = backStack)
        }
    }
}
