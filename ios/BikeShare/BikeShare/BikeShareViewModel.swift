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
        repository.fetchNetworkList(success: { data in
            self.networkList = data
        }) { (result, error) in
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

