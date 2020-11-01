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
        repository.fetchGroupedNetworkList(success: { data in
            self.networkList = data
        }) { (result, error) in
            if let errorReal = error {
                print(errorReal)
            }
        }
    }

    func fetch(network: String) {
        repository.fetchBikeShareInfo(network: network) { data, error in
            if let stationList = data {
                self.stationList = stationList
            }
        }
    }
}

