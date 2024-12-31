import SwiftUI
import MapKit
import BikeShareKit
import KMPObservableViewModelCore
import KMPObservableViewModelSwiftUI


struct ContentView : View {
    let applicationComponent: IosApplicationComponent
    
    @ObservedViewModel var viewModel: CountriesViewModelShared
    @State var query: String = ""
    
    init(applicationComponent: IosApplicationComponent) {
        self.applicationComponent = applicationComponent
        self.viewModel = applicationComponent.countriesViewModel
    }
    
    var body: some View {
        NavigationView {
            List {
                ForEach(viewModel.countryList.filter { query.isEmpty ||  $0.displayName.contains(query)}, id: \.self) { country in
                    NavigationLink(destination: NetworkListView(applicationComponent: applicationComponent, countryCode: country.code)) {
                        HStack {
                            Text(countryFlag(from: country.code))
                            Text(country.displayName).font(.headline)
                        }
                    }
                }
            }
            .searchable(text: $query)
            .navigationTitle("Bike Share")
        }
    }
}
    


struct NetworkListView: View {
    let applicationComponent: IosApplicationComponent
    let countryCode: String
    
    @ObservedViewModel var viewModel: NetworksViewModelShared

    init(applicationComponent: IosApplicationComponent, countryCode: String) {
        self.applicationComponent = applicationComponent
        self.countryCode = countryCode
        self.viewModel = applicationComponent.networksViewModel
    }
    
    var body: some View {
        List(viewModel.networkList) { network in
            NavigationLink(destination: StationListTabView(applicationComponent: applicationComponent, network: network)) {
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
    let applicationComponent: IosApplicationComponent
    var network: Network
    
    @ObservedViewModel var viewModel: StationsViewModelShared

    init(applicationComponent: IosApplicationComponent, network: Network) {
        self.applicationComponent = applicationComponent
        self.network = network
        self.viewModel = applicationComponent.stationsViewModel
    }

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
        .navigationTitle(network.id)
        .onAppear {
            viewModel.setNetwork(networkId: network.id)
        }
    }
}


struct StationListView: View {
    var stations : [Station]
    var network: Network
    
    var body: some View {
        List(stations.sorted { $0.name < $1.name }) { station in
            StationView(station: station)
        }
        .navigationTitle(network.name)
    }
}

struct StationView : View {
    var station: Station

    var body: some View {
        
        HStack {
            
            VStack(alignment: .trailing) {
                Image("ic_bike").resizable()
                    .renderingMode(.template)
                    .foregroundColor(station.freeBikes() < 2 ? .orange : .green)
                    .frame(width: 32.0, height: 32.0)
            }

            Spacer().frame(width: 16)

            VStack(alignment: .leading) {
                Text(station.name).bold()
                
                HStack {
                    VStack(alignment: .leading) {
                        Text("Bikes")
                        Text("\(station.freeBikes())").font(.subheadline).foregroundColor(station.freeBikes() < 2 ? .orange : .green)
                    }
                    
                    VStack(alignment: .leading) {
                        Text("Stands")
                        Text("\(station.slots())").font(.subheadline).foregroundColor(station.freeBikes() < 2 ? .orange : .green)
                    }
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
                                   
