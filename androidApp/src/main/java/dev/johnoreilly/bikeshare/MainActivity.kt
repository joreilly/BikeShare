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
import dev.johnoreilly.bikeshare.ui.theme.BikeShareTheme
import dev.johnoreilly.common.di.AndroidApplicationComponent


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val applicationComponent = (applicationContext as BikeShareApplication).component

        setContent {
            BikeShareTheme {
                BikeShareApp(applicationComponent)
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
fun BikeShareApp(applicationComponent: AndroidApplicationComponent) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        Row(Modifier.padding(innerPadding)) {
            val bikeNetwork = BuildConfig.BIKE_NETWORK
            if (bikeNetwork.isNotEmpty()) {
                val stationsScreen = applicationComponent.stationsScreen
                stationsScreen(bikeNetwork, null)
            } else {
                NavHost(navController, startDestination = Screen.CountryListScreen.title) {
                    val countryListScreen = applicationComponent.countryListScreen
                    composable(Screen.CountryListScreen.title) {
                        countryListScreen {
                            navController.navigate(Screen.NetworkListScreen.title + "/${it.code}")
                        }
                    }
                    composable(Screen.NetworkListScreen.title + "/{countryCode}") { backStackEntry ->
                        val networkListScreen = applicationComponent.networkListScreen
                        networkListScreen(backStackEntry.arguments?.getString("countryCode") as String,
                                { navController.navigate(Screen.StationsScreen.title + "/$it") },
                                { navController.popBackStack() })
                    }
                    composable(Screen.StationsScreen.title + "/{networkId}") { backStackEntry ->
                        val stationsScreen = applicationComponent.stationsScreen
                        stationsScreen(backStackEntry.arguments?.getString("networkId") as String,
                                { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
