import Foundation
import common


class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [Network]()
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
    }
 
    func fetchNetworks() {
        repository.fetchNetworkList() { data, error in
            if let networkList = data {
                self.networkList = networkList
            }
            if let errorReal = error {
               print(errorReal)
            }
        }
    }

    
    func fetchBikeShareInfo(network: String) {
        repository.fetchBikeShareInfo(network: network) { data, error in
            if let stationList = data {
                self.stationList = stationList
            }
            if let errorReal = error {
               print(errorReal)
            }
        }
    }
}

