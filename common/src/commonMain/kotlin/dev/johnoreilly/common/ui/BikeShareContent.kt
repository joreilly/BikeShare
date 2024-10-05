package dev.johnoreilly.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.johnoreilly.common.screens.CountryListScreen
import me.tatarka.inject.annotations.Inject



typealias BikeShareApp = @Composable () -> Unit

@Inject
@Composable
fun BikeShareApp(circuit: Circuit) {
    MaterialTheme {
        val backStack = rememberSaveableBackStack(root = CountryListScreen)
        val navigator = rememberCircuitNavigator(backStack) {}

        CircuitCompositionLocals(circuit) {
            NavigableCircuitContent(navigator = navigator, backStack = backStack)
        }
    }
}
