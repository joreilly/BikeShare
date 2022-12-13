import Foundation
import common
import KMPNativeCoroutinesAsync

@MainActor
class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [String: [Network]]()
    
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
        
        fetchNetworks()
    }
 
    func fetchNetworks() {
        Task {
            do {
                let stream = asyncSequence(for: repository.groupedNetworkList)
                for try await data in stream {
                    self.networkList = data
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    
    func startObservingBikeShareInfo(network: String) async {
        do {
            let stream = asyncSequence(for: repository.pollNetworkUpdates(network: network))
            for try await data in stream {
                self.stationList = data
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }
    
}

