import Foundation
import ActivityKit
import common

struct BikeShareAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var stationName: String
        var numberFreeBikes: Int
        var numberOfSlots: Int
    }
}




