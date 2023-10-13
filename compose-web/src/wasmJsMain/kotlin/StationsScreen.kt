import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


val lowAvailabilityColor = Color(0xFFFF8C00)
val highAvailabilityColor = Color(0xFF008000)


@Composable
fun StationsScreen(stationList: List<Station>) {

    LazyColumn {
        items(stationList) { station ->
            StationView(station)
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun StationView(station: Station) {

    Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp).fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically) {

        Column(modifier = Modifier.weight(1.0f)) {
            Text(text = station.name, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)

            ProvideTextStyle(MaterialTheme.typography.body1) {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Column(modifier = Modifier.width(100.dp)) {
                        Text("Bikes")
                        Text(
                            station.freeBikes().toString(),
                            color = if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor
                        )
                    }

                    Column {
                        Text("Stands")
                        Text(
                            station.empty_slots.toString(),
                            color = if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Image(
                imageVector = rememberBikeIcon(),
//                  painterResource("ic_bike.xml"),
                colorFilter = ColorFilter.tint(if (station.freeBikes() < 5) lowAvailabilityColor else highAvailabilityColor),
                modifier = Modifier.size(32.dp),
                contentDescription = station.freeBikes().toString()
            )
        }
    }
}

@Composable
fun rememberBikeIcon(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "newIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.5f, 5.5f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                close()
                moveTo(5f, 12f)
                curveToRelative(-2.8f, 0f, -5f, 2.2f, -5f, 5f)
                reflectiveCurveToRelative(2.2f, 5f, 5f, 5f)
                reflectiveCurveToRelative(5f, -2.2f, 5f, -5f)
                reflectiveCurveToRelative(-2.2f, -5f, -5f, -5f)
                close()
                moveTo(5f, 20.5f)
                curveToRelative(-1.9f, 0f, -3.5f, -1.6f, -3.5f, -3.5f)
                reflectiveCurveToRelative(1.6f, -3.5f, 3.5f, -3.5f)
                reflectiveCurveToRelative(3.5f, 1.6f, 3.5f, 3.5f)
                reflectiveCurveToRelative(-1.6f, 3.5f, -3.5f, 3.5f)
                close()
                moveTo(10.8f, 10.5f)
                lineToRelative(2.4f, -2.4f)
                lineToRelative(0.8f, 0.8f)
                curveToRelative(1.3f, 1.3f, 3f, 2.1f, 5.1f, 2.1f)
                lineTo(19.1f, 9f)
                curveToRelative(-1.5f, 0f, -2.7f, -0.6f, -3.6f, -1.5f)
                lineToRelative(-1.9f, -1.9f)
                curveToRelative(-0.5f, -0.4f, -1f, -0.6f, -1.6f, -0.6f)
                reflectiveCurveToRelative(-1.1f, 0.2f, -1.4f, 0.6f)
                lineTo(7.8f, 8.4f)
                curveToRelative(-0.4f, 0.4f, -0.6f, 0.9f, -0.6f, 1.4f)
                curveToRelative(0f, 0.6f, 0.2f, 1.1f, 0.6f, 1.4f)
                lineTo(11f, 14f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-6.2f)
                lineToRelative(-2.2f, -2.3f)
                close()
                moveTo(19f, 12f)
                curveToRelative(-2.8f, 0f, -5f, 2.2f, -5f, 5f)
                reflectiveCurveToRelative(2.2f, 5f, 5f, 5f)
                reflectiveCurveToRelative(5f, -2.2f, 5f, -5f)
                reflectiveCurveToRelative(-2.2f, -5f, -5f, -5f)
                close()
                moveTo(19f, 20.5f)
                curveToRelative(-1.9f, 0f, -3.5f, -1.6f, -3.5f, -3.5f)
                reflectiveCurveToRelative(1.6f, -3.5f, 3.5f, -3.5f)
                reflectiveCurveToRelative(3.5f, 1.6f, 3.5f, 3.5f)
                reflectiveCurveToRelative(-1.6f, 3.5f, -3.5f, 3.5f)
                close()
            }
        }.build()
    }
}