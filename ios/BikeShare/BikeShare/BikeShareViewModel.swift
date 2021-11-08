import Foundation
import common
import KMPNativeCoroutinesAsync
import CollectionConcurrencyKit

@MainActor
class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [Network]()
    
    private var fetchStationsTask: Task<(), Never>? = nil
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
    }
 
    func fetchNetworks() {
        Task {
            let result = await asyncResult(for: repository.fetchNetworkListNative())
            if case let .success(networkList) = result {
                self.networkList = await networkList.concurrentMap { network -> Network? in
                    let result = await asyncResult(for: self.repository.fetchNetworkNative(network: network.id))
                    if case let .success(network) = result {
                        return network
                    } else {
                        return nil
                    }
                }.compactMap { $0 }
            }
        }
    }
    
    
    func startObservingBikeShareInfo(network: String) {
        
        fetchStationsTask = Task {
            do {
                let stream = asyncStream(for: repository.pollNetworkUpdatesNative(network: network))
                for try await data in stream {
                    self.stationList = data
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    func stopObservingBikeShareInfo() {
        fetchStationsTask?.cancel()
    }
}

