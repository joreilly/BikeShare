package com.surrus.bikeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.surrus.bikeshare.ui.BikeShareTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainLayout()
        }
    }
}


sealed class Screen(val title: String) {
    object CountryListScreen : Screen("CountryList")
    object NetworkListScreen : Screen("NetworkList")
    object StationsScreen : Screen("Stations")
}


@Composable
fun MainLayout() {
    val navController = rememberNavController()

    BikeShareTheme {
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
