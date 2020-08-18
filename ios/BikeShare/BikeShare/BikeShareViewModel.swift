import Foundation
import common


class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
    }
    
    func fetch(network: String) {
        repository.fetchBikeShareInfo(network: network) { data, error in
            if let stationList = data {
                self.stationList = stationList
            }
        }
    }
}

