import Foundation
import ActivityKit
import common
import KMPNativeCoroutinesAsync

@MainActor
class CityBikesViewModel: ObservableObject {
    @Published var stationList = [Station]()
    @Published var networkList = [String: [Network]]()
    
    @Published var liveActivity: Activity<BikeShareAttributes>?
    
    
    private let repository: CityBikesRepository
    init(repository: CityBikesRepository) {
        self.repository = repository
        
        fetchNetworks()
    }
 
    func fetchNetworks() {
        Task {
            do {
                let stream = asyncStream(for: repository.groupedNetworkListNative)
                for try await data in stream {
                    self.networkList = data
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    
    func startObservingBikeShareInfo(network: String)  {
        Task {
            do {
                let stream = asyncStream(for: repository.pollNetworkUpdatesNative(network: network))
                for try await data in stream {
                    self.stationList = data
                    
                    let station = data[1]
                    print(station)
                    updateStationInfo(station: station)
                    
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    
    
    func startActivity(stationName: String) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else {
            print("Activities are not enabled.")
            return
        }
        Task {
            let attributes = BikeShareAttributes()
            let state = BikeShareAttributes.ContentState(stationName: "", numberFreeBikes: 0, numberOfSlots: 0)
            do {
                try await MainActor.run {
                    liveActivity = try Activity<BikeShareAttributes>.request(
                        attributes: attributes,
                        contentState: state,
                        pushType: nil
                    )
                }
                print("Started activity")
            } catch (let error) {
                print("Error starting activity \(error) \(error.localizedDescription)")
            }
        }
    }

    func updateStationInfo(station: Station) {
        let state = BikeShareAttributes.ContentState(stationName: station.name, numberFreeBikes: Int(station.freeBikes()), numberOfSlots: Int(station.slots()))
        Task {
            await liveActivity?.update(using: state)
            print("activity updated")
        }
    }

    func stop() {
        Task {
            await liveActivity?.end(using: nil, dismissalPolicy: .immediate)
            await MainActor.run {
                liveActivity = nil
            }
        }
    }
}

