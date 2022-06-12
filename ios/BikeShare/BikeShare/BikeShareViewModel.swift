import Foundation
import common
import KMPNativeCoroutinesAsync


extension Station: Identifiable { }
extension Network: Identifiable { }


@MainActor
class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [String: [Network]]()
    @Published var countryList = [String]()
    
    @Published var selectedCountry: String?
    @Published var selectedNetwork: Network?
    
    
    private let repository: CityBikesRepository
    init() {
        self.repository = CityBikesRepository()        
        fetchNetworks()
    }
 
    func fetchNetworks() {
        Task {
            do {
                let stream = asyncStream(for: repository.groupedNetworkListNative)
                for try await data in stream {
                    self.networkList = data
                    self.countryList = Array(networkList.keys.sorted(by: { countryName(from: $0) < countryName(from: $1)}) )
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    func getNetworks(countryCode: String?) -> [Network]? {
        if let countryCode {
            return networkList[countryCode]
        } else{
            return nil
        }
    }
    
    func getSortedNetworks(countryCode: String?) -> [Network]? {
        if let countryCode {
            return getNetworks(countryCode: countryCode)?.sorted(by: { $0.city < $1.city })
        } else{
            return nil
        }
    }

    
    func startObservingBikeShareInfo(network: String) async {
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

