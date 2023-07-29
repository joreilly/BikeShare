import SwiftUI
import MapKit
import common
import KMMViewModelCore
import KMMViewModelSwiftUI


struct ContentView : View {
    @StateViewModel var viewModel = CountriesViewModelShared()
    
    var body: some View {
        NavigationView {
            List {
                ForEach(viewModel.countryList, id: \.self) { country in
                    NavigationLink(destination: NetworkListView(countryCode: country.code)) {
                        HStack {
                            Text(countryFlag(from: country.code))
                            Text(country.displayName).font(.headline)
                        }
                    }
                }
            }
            .navigationTitle("Bike Share")
        }
    }
}
    


struct NetworkListView: View {
    @StateViewModel var viewModel = NetworksViewModelShared()
    let countryCode: String

    var body: some View {
        List(viewModel.networkList) { network in
            NavigationLink(destination: StationListTabView(network: network)) {
                Text("\(network.name) (\(network.city))").font(.subheadline)
            }
        }
        .navigationTitle(countryName(from: countryCode))
        .onAppear {
            viewModel.setCountryCode(countryCode: countryCode)
        }
    }
}


struct StationListTabView: View {
    @StateViewModel var viewModel = StationsViewModelShared()
    var network: Network

    var body: some View {
        TabView {
            StationListView(stations: viewModel.stations, network: network)
                .tabItem {
                    Label("List", systemImage: "list.dash")
                }
            StationMapView(stations: viewModel.stations, network: network)
                .tabItem {
                    Label("Map", systemImage: "location")
                }
        }
        .navigationTitle(network.name)
        .onAppear {
            viewModel.setNetwork(networkId: network.id)
        }
    }
}


struct StationListView: View {
    var stations : [Station]
    var network: Network
    
    var body: some View {
        List(stations) { station in
            StationView(station: station)
        }
        .navigationTitle(network.name)
    }
}

struct StationView : View {
    var station: Station

    var body: some View {
        HStack {
            Image("ic_bike").resizable()
                .renderingMode(.template)
                .foregroundColor(station.freeBikes() < 5 ? .orange : .green)
                .frame(width: 32.0, height: 32.0)
            
            Spacer().frame(width: 16)
            
            VStack(alignment: .leading) {
                Text(station.name).font(.headline)
                HStack {
                    Text("Free:").font(.subheadline).frame(width: 80, alignment: .leading)
                    Text("\(station.freeBikes())").font(.subheadline)
                }
                HStack {
                    Text("Slots:").font(.subheadline).frame(width: 80, alignment: .leading)
                    Text("\(station.slots())").font(.subheadline)
                }
            }
        }
        .navigationTitle(station.name)
    }
}


struct StationMapView : View {
    var stations : [Station]
    var network: Network
    @State var region = MKCoordinateRegion(center: .init(latitude: 0, longitude: 0),
                                           latitudinalMeters: 5000, longitudinalMeters: 5000)

    var body: some View {
        VStack {
            Text(network.city)
            Map(coordinateRegion: $region,
                interactionModes: MapInteractionModes.all,
                showsUserLocation: true,
                annotationItems: stations) { (station) -> MapPin in
                    let coordinate = CLLocationCoordinate2D(latitude: station.latitude,
                                                            longitude: station.longitude)
                return MapPin(coordinate: coordinate, tint: station.freeBikes() < 5 ? Color.red : Color.green)
            }
        }
        .onAppear(perform: {
            region.center = CLLocationCoordinate2D(latitude: network.latitude,
                                                   longitude: network.longitude)
        })
    }
}


func countryName(from countryCode: String) -> String {
    if let name = (Locale.current as NSLocale).displayName(forKey: .countryCode, value: countryCode) {
        return name
    } else {
        return countryCode
    }
}

func countryFlag(from countryCode: String) -> String {
  let base = 127397
  var tempScalarView = String.UnicodeScalarView()
  for i in countryCode.utf16 {
    if let scalar = UnicodeScalar(base + Int(i)) {
      tempScalarView.append(scalar)
    }
  }
  return String(tempScalarView)
}
    

extension Station: Identifiable { }
extension Network: Identifiable { }
                                   
