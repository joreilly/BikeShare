package com.surrus.bikeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.surrus.bikeshare.glance.BikeShareAppWidget
import com.surrus.bikeshare.ui.BikeShareTheme
import com.surrus.common.repository.CityBikesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    lateinit var manager: GlanceAppWidgetManager
    private val cityBikesRepository: CityBikesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manager = GlanceAppWidgetManager(this)

        // adding this here just as test for now....need to see where best place to do
        // this would be
        lifecycleScope.launch {
            cityBikesRepository.pollNetworkUpdates("galway").collect {
                manager.getGlanceIds(BikeShareAppWidget::class.java).forEach { id ->
                    // use first station for now
                    BikeShareAppWidget(it[0]).update(this@MainActivity, id)
                }
            }
        }

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
