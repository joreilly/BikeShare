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

//@main
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



@main
struct BikeShareWidgets: WidgetBundle {
    var body: some Widget {
        // Any other widgets
        BikeShareWidget()
        BikeShareActivityWidget()
    }
}


struct BikeShareActivityWidget: Widget {
    var body: some WidgetConfiguration {
        return ActivityConfiguration(for: BikeShareAttributes.self) { context in
            BikeShareActivityWidgetView(
                attributes: context.attributes,
                state: context.state
            )
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.center, priority: .greatestFiniteMagnitude, content: {
                    HStack(alignment: .center) {
                        VStack {
                            Text("\(context.state.stationName)")
                                .font(.caption.weight(.bold))
                                .lineLimit(nil)
                                .multilineTextAlignment(.leading)
                                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                            Text("Free bikes: \(context.state.numberFreeBikes)")
                                .font(.caption.weight(.semibold))
                                .lineLimit(nil)
                                .multilineTextAlignment(.leading)
                                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                        }
                        Image("ic_bike").resizable()
                            .renderingMode(.template)
                            .foregroundColor(context.state.numberFreeBikes < 5 ? .orange : .green)
                            .frame(width: 48.0, height: 48.0)

                    }
                })
            } compactLeading: {
                Image("ic_bike").resizable()
                    .renderingMode(.template)
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(context.state.numberFreeBikes < 5 ? .orange : .green)
                    .padding(6)
            } compactTrailing: {
                ZStack {
                    Text("\(context.state.numberFreeBikes)")
                        .font(.caption.weight(.semibold))
                        .lineLimit(nil)
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                }
                .padding(.vertical, 6)
                
            } minimal: {
                
            }
        }
    }
}



struct BikeShareActivityWidgetView: View {
    let attributes: BikeShareAttributes
    let state: BikeShareAttributes.ContentState

    var body: some View {        
        VStack {
            Text(state.stationName).font(.headline)
            HStack {
                Spacer()
                Image("ic_bike").resizable()
                    .renderingMode(.template)
                    .foregroundColor(state.numberFreeBikes < 5 ? .orange : .green)
                    .frame(width: 48.0, height: 48.0)
                
                Spacer().frame(width: 32)
                
                VStack(alignment: .leading) {
                    HStack {
                        Text("Free:").font(.subheadline).frame(width: 80, alignment: .leading)
                        Text("\(state.numberFreeBikes)").font(.subheadline)
                    }
                    HStack {
                        Text("Slots:").font(.subheadline).frame(width: 80, alignment: .leading)
                        Text("\(state.numberOfSlots)").font(.subheadline)
                    }
                }
                Spacer()
            }
        }
        .padding()
    }
}
