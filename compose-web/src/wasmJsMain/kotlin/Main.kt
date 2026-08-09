import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.johnoreilly.common.di.WebApplicationComponent
import dev.johnoreilly.common.di.create

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val applicationComponent = WebApplicationComponent::class.create()

    ComposeViewport {
        applicationComponent.bikeShareApp()
    }
}
