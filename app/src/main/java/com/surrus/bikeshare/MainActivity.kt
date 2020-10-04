package com.surrus.bikeshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.setContent
import com.surrus.bikeshare.ui.BikeShareTheme
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.Router


class MainActivity : AppCompatActivity() {
    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Providers(AmbientBackPressHandler provides backPressHandler) {
                MainLayout(Routing.StationsScreenRoute)
            }
        }
    }
}


sealed class Routing(val title: String) {
    object StationsScreenRoute: Routing("Stations")
    object NetworkScreenRoute: Routing("Networks")
}


@Composable
fun MainLayout(defaultRouting: Routing) {
    val screenList = listOf(Routing.StationsScreenRoute, Routing.NetworkScreenRoute)

    BikeShareTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Bike Share") }) },
            bodyContent = { innerPadding ->
                Column {
                    Router(defaultRouting) { backStack ->
                        HomeTabs(screenList, backStack.last()) {
                            backStack.push(it)
                        }

                        when (backStack.last()) {
                            is Routing.StationsScreenRoute -> StationsScreen()
                            is Routing.NetworkScreenRoute -> NetworksScreen()
                        }
                    }
                }
            }
        )
    }
}



@Composable
private fun HomeTabs(screenList: List<Routing>, selectedScreen: Routing, onScreenSelected: (Routing) -> Unit) {
    val selectedIndex = screenList.indexOfFirst { it == selectedScreen }

    TabRow(selectedTabIndex = selectedIndex) {
        screenList.forEachIndexed { index, screen ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onScreenSelected(screen) },
                text = { Text(screen.title, style = MaterialTheme.typography.body2) }
            )
        }
    }
}



