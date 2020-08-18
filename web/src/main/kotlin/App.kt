import com.surrus.common.remote.Station
import react.*
import react.dom.*
import kotlinx.coroutines.*
import react.router.dom.*


val App = functionalComponent<RProps> { _ ->

    browserRouter {
        div {
            ul {
                li {
                    routeLink("/galway") { +"Galway" }
                }
                li {
                    routeLink("/oslo-bysykkel") { +"Oslo" }
                }
            }
        }

        div {
            switch {
                route("/galway") { child(StationList) {
                        attrs { networkId = "galway" }
                    }
                }

                route("/oslo-bysykkel") { child(StationList) {
                        attrs { networkId = "oslo-bysykkel" }
                    }
                }
            }
        }
    }
}


external interface NetworkProps : RProps {
    var networkId: String
}

val StationList = functionalComponent<NetworkProps> { props ->
    val networkId = props.networkId

    val appDependencies = useContext(AppDependenciesContext)
    val repository = appDependencies.repository

    val (stations, setStations) = useState(emptyList<Station>())

    useEffectWithCleanup {
        console.log("Network Id $networkId")

        val mainScope = MainScope()

        mainScope.launch {
            setStations(repository.fetchBikeShareInfo(networkId))
        }
        return@useEffectWithCleanup { mainScope.cancel() }
    }

    h1 {
        +"Bike Share - $networkId"
    }
    ul {
        stations.forEach { station ->
            li {
                +"${station.name} (${station.free_bikes})"
            }
        }
    }
}
