package dev.johnoreilly.bikeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.johnoreilly.bikeshare.ui.CountryListScreen
import dev.johnoreilly.bikeshare.ui.NetworkListScreen
import dev.johnoreilly.bikeshare.ui.StationsScreen
import dev.johnoreilly.bikeshare.ui.theme.BikeShareTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            BikeShareTheme {
                BikeShareApp()
            }
        }
    }
}

sealed class Screen(val title: String) {
    data object CountryListScreen : Screen("CountryList")
    data object NetworkListScreen : Screen("NetworkList")
    data object StationsScreen : Screen("Stations")
}


@Composable
fun BikeShareApp() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        Row(Modifier.padding(innerPadding)) {
            val bikeNetwork = BuildConfig.BIKE_NETWORK
            if (bikeNetwork.isNotEmpty()) {
                StationsScreen(bikeNetwork, popBack = null)
            } else {
                NavHost(navController, startDestination = Screen.CountryListScreen.title) {
                    composable(Screen.CountryListScreen.title) {
                        CountryListScreen {
                            navController.navigate(Screen.NetworkListScreen.title + "/${it.code}")
                        }
                    }
                    composable(Screen.NetworkListScreen.title + "/{countryCode}") { backStackEntry ->
                        NetworkListScreen(backStackEntry.arguments?.getString("countryCode") as String,
                            networkSelected = { navController.navigate(Screen.StationsScreen.title + "/$it") },
                            popBack = { navController.popBackStack() })
                    }
                    composable(Screen.StationsScreen.title + "/{networkId}") { backStackEntry ->
                        StationsScreen(backStackEntry.arguments?.getString("networkId") as String,
                            popBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
