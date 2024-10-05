import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.johnoreilly.common.di.DesktopApplicationComponent
import dev.johnoreilly.common.di.create


fun main() {
    val applicationComponent = DesktopApplicationComponent.create()

    return singleWindowApplication(
        title = "BikeShare",
        state = WindowState(size = DpSize(400.dp, 600.dp))
    ) {
        applicationComponent.bikeShareApp()
    }
}

