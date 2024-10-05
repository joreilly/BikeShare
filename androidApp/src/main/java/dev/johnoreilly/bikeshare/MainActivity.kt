package dev.johnoreilly.bikeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.johnoreilly.bikeshare.ui.theme.BikeShareTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val applicationComponent = (applicationContext as BikeShareApplication).component

        setContent {
            BikeShareTheme {
                applicationComponent.bikeShareApp()
            }
        }
    }
}


/*

TODO: support following using Circuit

            val bikeNetwork = BuildConfig.BIKE_NETWORK
            if (bikeNetwork.isNotEmpty()) {
                val stationsScreen = applicationComponent.stationsScreen
                stationsScreen(bikeNetwork, null)
            }

 */