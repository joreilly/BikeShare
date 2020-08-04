import com.surrus.common.repository.CityBikesRepository
import kotlinx.browser.document
import react.child
import react.createContext
import react.dom.render

object AppDependencies {
    val repository = CityBikesRepository()
}

val AppDependenciesContext = createContext<AppDependencies>()


fun main() {
    render(document.getElementById("root")) {
        AppDependenciesContext.Provider(AppDependencies) {
            child(App)
        }
    }
}