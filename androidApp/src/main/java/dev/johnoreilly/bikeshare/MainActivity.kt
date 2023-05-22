@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.bikeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.johnoreilly.bikeshare.ui.CountryListScreen
import dev.johnoreilly.bikeshare.ui.NetworkListScreen
import dev.johnoreilly.bikeshare.ui.StationsScreen
import dev.johnoreilly.bikeshare.ui.theme.BikeShareBackground
import dev.johnoreilly.bikeshare.ui.theme.BikeShareTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BikeShareTheme {
                BikeShareBackground {
                    BikeShareApp()
                }
            }
        }
    }
}

sealed class Screen(val title: String) {
    object CountryListScreen : Screen("CountryList")
    object NetworkListScreen : Screen("NetworkList")
    object StationsScreen : Screen("Stations")
}


@Composable
fun BikeShareApp() {
    val navController = rememberNavController()


    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            val bikeNetwork = BuildConfig.BIKE_NETWORK
            if (bikeNetwork.isNotEmpty()) {
                StationsScreen(bikeNetwork, popBack = null)
            } else {
                NavHost(navController, startDestination = Screen.CountryListScreen.title) {
                    composable(Screen.CountryListScreen.title) {
                        CountryListScreen {
                            navController.navigate(Screen.NetworkListScreen.title + "/$it")
                        }
                    }
                    composable(Screen.NetworkListScreen.title + "/{countryCode}") { backStackEntry ->
                        NetworkListScreen(backStackEntry.arguments?.get("countryCode") as String,
                            networkSelected = { navController.navigate(Screen.StationsScreen.title + "/$it") },
                            popBack = { navController.popBackStack() })
                    }
                    composable(Screen.StationsScreen.title + "/{networkId}") { backStackEntry ->
                        StationsScreen(backStackEntry.arguments?.get("networkId") as String,
                            popBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
