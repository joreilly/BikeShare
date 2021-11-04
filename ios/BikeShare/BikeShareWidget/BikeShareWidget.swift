import WidgetKit
import SwiftUI
import common
import Combine
import KMPNativeCoroutinesAsync


final class Provider: TimelineProvider {
    
    var timelineCancellable: AnyCancellable?

    private var entryPublisher: AnyPublisher<BikeShareStationEntry, Never> {

        let future = Future<BikeShareStationEntry, Never> { promise in
            let repository =  CityBikesRepository()

            repository.fetchBikeShareInfo(network: "galway") { data, error in
                if let stationList = data {
                    promise(.success(BikeShareStationEntry(date: Date(), station: stationList[0])))
                }
                if let errorReal = error {
                    print(errorReal)
                }
            }
        }
        return AnyPublisher(future)
    }
    
    init() {
        KoinKt.doInitKoin()
    }

    func placeholder(in context: Context) -> BikeShareStationEntry {
        BikeShareStationEntry(date: Date(), station: nil)
    }

    func getSnapshot(in context: Context, completion: @escaping (BikeShareStationEntry) -> ()) {
        let entry = BikeShareStationEntry(date: Date(), station: nil)
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        timelineCancellable = entryPublisher
            .map { Timeline(entries: [$0], policy: .atEnd) }
            .receive(on: DispatchQueue.main)
            .sink(receiveValue: completion)
    }
}

struct BikeShareStationEntry: TimelineEntry {
    let date: Date
    let station: Station?
}

struct BikeShareWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        if let station = entry.station {
            VStack() {
                Text(station.name).font(.headline)
                Image("ic_bike").resizable()
                    .renderingMode(.template)
                    .foregroundColor(station.freeBikes() < 5 ? .orange : .green)
                    .frame(width: 32.0, height: 32.0)

                HStack() {
                    Text("Free:").font(.subheadline).frame(width: 80, alignment: .leading)
                    Text("\(station.freeBikes())").font(.subheadline)
                }
                HStack {
                    Text("Total:").font(.subheadline).frame(width: 80, alignment: .leading)
                    Text("\(station.slots())").font(.subheadline)
                }
            }
        }
    }
}

@main
struct BikeShareWidget: Widget {
    let kind: String = "BikeShareWidget"


    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            BikeShareWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("BikeShare")
        .description("Bike Share Widget")
    }
}

