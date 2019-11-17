import Foundation
import common


class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
    }
    
    func fetch() {
        repository.fetchBikeShareInfo(network: "galway", success: { data in
            self.stationList = data.stations
        })
    }
}

