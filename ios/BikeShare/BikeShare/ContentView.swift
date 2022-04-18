import SwiftUI
import MapKit
import common


struct ContentView : View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
    @State private var selection = 0
    
    var body: some View {
        NavigationView {
            List {
                ForEach(Array(cityBikesViewModel.networkList.keys.sorted(by: { countryName(from: $0) < countryName(from: $1)}) ), id: \.self) { countryCode in
                    NavigationLink(destination: StationListView(cityBikesViewModel: cityBikesViewModel,
                                        country: countryName(from: countryCode),
                                        networks: cityBikesViewModel.networkList[countryCode]!))
                    {
                        HStack {
                            Text(countryFlag(from: countryCode))
                            Text(countryName(from: countryCode)).font(.headline)
                        }
                    }
                }
            }
            .navigationTitle("Bike Share")
        }
        
    }
}
    
func countryName(from countryCode: String) -> String {
    if let name = (Locale.current as NSLocale).displayName(forKey: .countryCode, value: countryCode) {
        // Country name was found
        return name
    } else {
        // Country name cannot be found
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


struct StationListView: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    let country: String
    let networks: [Network]

    var body: some View {
        List(networks.sorted(by: { $0.city < $1.city }), id: \.id) { network in
            NavigationLink(destination: BikeNetworkView(cityBikesViewModel: cityBikesViewModel,network: network)) {
                Text("\(network.name) (\(network.city))").font(.subheadline)
            }

        }
        .navigationTitle(country)
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


struct BikeNetworkView : View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    var network: Network
    @State var region = MKCoordinateRegion(center: .init(latitude: 0, longitude: 0),
                                           latitudinalMeters: 5000, longitudinalMeters: 5000)

    var body: some View {
        VStack {
            Text(network.city)
            Map(coordinateRegion: $region,
                interactionModes: MapInteractionModes.all,
                showsUserLocation: true,
                annotationItems: self.cityBikesViewModel.stationList) { (station) -> MapPin in
                    let coordinate = CLLocationCoordinate2D(latitude: station.latitude,
                                                            longitude: station.longitude)
                return MapPin(coordinate: coordinate, tint: station.freeBikes() < 5 ? Color.red : Color.green)
            }
        }
        .onAppear(perform: {
            region.center = CLLocationCoordinate2D(latitude: network.latitude,
                                                   longitude: network.longitude)
        })
        .task {
            await cityBikesViewModel.startObservingBikeShareInfo(network: network.id)
        }
    }
}
