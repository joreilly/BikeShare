import Foundation
import common


class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [String: [Network]]()
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
    }

    
    func fetchNetworkList() {
        repository.fetchNetworkList(success: { data in
            self.networkList = data
        })
    }

    func fetch(network: String) {
        repository.fetchBikeShareInfo(network: network, success: { data in
            self.stationList = data.stations
        })
    }
}

