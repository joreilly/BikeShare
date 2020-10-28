package com.surrus.bikeshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.setContent
import androidx.core.os.bundleOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.surrus.bikeshare.ui.BikeShareTheme


class MainActivity : AppCompatActivity() {

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
        NavHost(navController, startDestination = Screen.CountryListScreen.title) {
            composable(Screen.CountryListScreen.title) {
                CountryListScreen {
                    navController.navigate(Screen.NetworkListScreen.title + "/$it")
                }
            }
            composable(Screen.NetworkListScreen.title + "/{countryCode}") { backStackEntry ->
                NetworkListScreen(navController, backStackEntry.arguments?.get("countryCode") as String) {
                    navController.navigate(Screen.StationsScreen.title + "/$it")
                }
            }
            composable(Screen.StationsScreen.title + "/{networkId}") { backStackEntry ->
                StationsScreen(navController, backStackEntry.arguments?.get("networkId") as String)
            }
        }
    }
}
