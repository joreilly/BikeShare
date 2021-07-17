import Foundation
import common
import KMPNativeCoroutinesAsync

@MainActor
class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [Network]()
    
    private let repository: CityBikesRepository
    
    init(repository: CityBikesRepository) {
        self.repository = repository
        
        let handle = Task {
            do {
                let stream = asyncStream(for: repository.pollNetworkUpdatesNative(network: "galway"))
                for try await stations in stream {
                    print("Got stations: \(stations)")
                    self.stationList = stations
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
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
//            let handle = Task {
//                do {
//                    let stream = asyncStream(for: repository.pollNetworkUpdatesNative(network: network))
//                    for try await stations in stream {
//                        print("Got stations: \(stations)")
//                        self.stationList = stationList
//                    }
//                } catch {
//                    print("Failed with error: \(error)")
//                }
//            }

        
//        repository.fetchBikeShareInfo(network: network) { data, error in
//            if let stationList = data {
//                self.stationList = stationList
//            }
//            if let errorReal = error {
//               print(errorReal)
//            }
//        }
    }
}

