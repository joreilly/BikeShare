import com.surrus.common.remote.Station
import react.*
import react.dom.*
import kotlinx.coroutines.*


val App = functionalComponent<RProps> { _ ->
    val appDependencies = useContext(AppDependenciesContext)
    val repository = appDependencies.repository

    val (stations, setStations) = useState(emptyList<Station>())

    useEffectWithCleanup(dependencies = listOf()) {
        val mainScope = MainScope()

        mainScope.launch {
            setStations(repository.fetchBikeShareInfo("galway").stations)
        }
        return@useEffectWithCleanup { mainScope.cancel() }
    }

    h1 {
        +"Bike Share - Galway"
    }
    ul {
        stations.forEach { station ->
            li {
                +"${station.name} (${station.free_bikes})"
            }
        }
    }
}
