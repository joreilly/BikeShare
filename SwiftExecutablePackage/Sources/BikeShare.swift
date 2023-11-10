import ArgumentParser
import KMPNativeCoroutinesAsync
import ANSITerminal
import BikeShareKit


@main
struct BikeShare: AsyncParsableCommand {
    @Argument(help: "the bike network")
    var network: String
    
    func run() async throws {
        KoinKt.doInitKoin()
        let repository = CityBikesRepository()
        
        let bikeStations = try await repository.fetchBikeShareInfo(network: network)
        bikeStations.forEach { bikeStation in
            let freeBikes = bikeStation.freeBikes()
            let stationInfo = "\(bikeStation.name): \(freeBikes)"
            if (freeBikes < 5) {
                print(stationInfo.red)
            } else {
                print(stationInfo.green)
            }
        }
    }
}

