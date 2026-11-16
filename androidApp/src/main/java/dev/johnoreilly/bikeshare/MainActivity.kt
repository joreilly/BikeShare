package dev.johnoreilly.bikeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dev.johnoreilly.bikeshare.agent.AskBikeShareOverlay
import dev.johnoreilly.bikeshare.agent.BikeShareAgentViewModel
import dev.johnoreilly.bikeshare.ui.theme.BikeShareTheme


class MainActivity : ComponentActivity() {

    private val agentViewModel: BikeShareAgentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val applicationComponent = (applicationContext as BikeShareApplication).component

        setContent {
            BikeShareTheme {
                AskBikeShareOverlay(agentViewModel) {
                    applicationComponent.bikeShareApp()
                }
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