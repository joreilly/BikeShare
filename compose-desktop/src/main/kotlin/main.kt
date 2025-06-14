import androidx.compose.material3.Text
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.johnoreilly.common.di.DesktopApplicationComponent
import dev.johnoreilly.common.di.create
import dev.sargunv.maplibrecompose.compose.KcefProvider
import dev.sargunv.maplibrecompose.compose.MaplibreContextProvider


fun main() {
    val applicationComponent = DesktopApplicationComponent::class.create()

    return singleWindowApplication(
        title = "BikeShare",
        state = WindowState(size = DpSize(400.dp, 600.dp))
    ) {
        KcefProvider(
            loading = { Text("Performing first time setup ...") },
            content = { MaplibreContextProvider {
                applicationComponent.bikeShareApp()
            } },
        )
    }
}

