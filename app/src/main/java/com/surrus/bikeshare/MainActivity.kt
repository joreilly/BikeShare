package com.surrus.bikeshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.LocationOn
import androidx.ui.res.vectorResource
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.surrus.bikeshare.ui.BikeShareTheme
import com.surrus.bikeshare.ui.highAvailabilityColor
import com.surrus.bikeshare.ui.lowAvailabilityColor
import com.surrus.bikeshare.ui.viewmodel.BikeShareViewModel
import com.surrus.common.remote.Station
import com.surrus.common.remote.freeBikes
import com.surrus.common.remote.slots
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val bikeShareViewModel: BikeShareViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BikeShareTheme {
                mainLayout(bikeShareViewModel)
            }
        }
    }
}

@Composable
fun mainLayout(bikeShareViewModel: BikeShareViewModel) {
    var bottomBarSelectedIndex by state { 0 }

    val stationsState = bikeShareViewModel.stations.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bike Share") })
        },
        bodyContent = {
            LazyColumnItems(items = stationsState.value) { station ->
                StationView(station)
            }
        },
        bottomBar = {
            BottomAppBar(content = {
                BottomNavigationItem(icon = { Icon(Icons.Default.LocationOn) }, text = { Text("Galway") },
                    selected = bottomBarSelectedIndex == 0,
                    onSelected = {
                        bottomBarSelectedIndex = 0
                        bikeShareViewModel.setCity("galway")
                    })

                BottomNavigationItem(icon = { Icon(Icons.Default.LocationOn) }, text = { Text("Oslo") },
                    selected = bottomBarSelectedIndex == 1,
                    onSelected = {
                        bottomBarSelectedIndex = 1
                        bikeShareViewModel.setCity("oslo-bysykkel")
                    })
            })
        }
    )
}


@Composable
fun StationView(station: Station) {
    Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalGravity = Alignment.CenterVertically) {

        Image(asset = vectorResource(R.drawable.ic_bike),
            colorFilter = ColorFilter.tint(if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor),
            modifier = Modifier.preferredSize(32.dp))

        Spacer(modifier = Modifier.preferredSize(16.dp))

        Column {

            Text(text = station.name, style = TextStyle(fontSize = 20.sp))
            Row {
                Text("Free:", modifier = Modifier.width(48.dp), fontSize = 16.sp)
                Text(text = station.freeBikes().toString(), style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
            }
            Row {
                Text("Slots:", modifier = Modifier.width(48.dp), fontSize = 16.sp)
                Text(text = station.slots().toString(), style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
            }
        }
    }
    Divider()
}

