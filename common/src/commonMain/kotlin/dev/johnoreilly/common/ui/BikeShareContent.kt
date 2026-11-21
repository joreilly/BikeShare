package dev.johnoreilly.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.johnoreilly.common.screens.CountryListScreen
import dev.johnoreilly.common.ui.map.LocalHttpClient
import dev.johnoreilly.common.ui.theme.BikeShareTheme
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Inject

typealias BikeShareApp = @Composable () -> Unit

@Inject
@Composable
fun BikeShareApp(circuit: Circuit, httpClient: HttpClient) {
    BikeShareTheme {
        CompositionLocalProvider(LocalHttpClient provides httpClient) {
            CircuitCompositionLocals(circuit) {
                // Must be inside CircuitCompositionLocals: rememberSaveableBackStack reads
                // LocalCircuitSaver, which CircuitCompositionLocals provides.
                val backStack = rememberSaveableBackStack(root = CountryListScreen)
                val navigator = rememberCircuitNavigator(
                    backStack = backStack,
                    onRootPop = {},
                    enableBackHandler = true,
                )

                NavigableCircuitContent(navigator = navigator, backStack = backStack)
            }
        }
    }
}
