package com.surrus.bikeshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.surrus.bikeshare.ui.BikeShareTheme
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.common.remote.slots
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.BackStack
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


sealed class Routing {
    object StationsScreenRoute: Routing()
    object NetworkScreenRoute: Routing()
}


@Composable
fun MainLayout(defaultRouting: Routing) {
    BikeShareTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Bike Share") }) },
            bodyContent = { innerPadding ->
                Column {
                    Router(defaultRouting) { backStack ->
                        HomeTabs {
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
private fun HomeTabs(onTabSelected: (Routing) -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }

    TabRow(selectedTabIndex = selectedIndex) {
        Tab(
            selected = 0 == selectedIndex,
            onClick = { onTabSelected(Routing.StationsScreenRoute); selectedIndex = 0 },
            text = { Text("Stations", style = MaterialTheme.typography.body2) }
        )
        Tab(
            selected = 1 == selectedIndex,
            onClick = { onTabSelected(Routing.NetworkScreenRoute); selectedIndex = 1 },
            text = { Text("Networks", style = MaterialTheme.typography.body2)  }
        )
    }
}



