import SwiftUI
import MapKit
import common


struct ContentView : View {
    @ObservedObject var viewModel = CityBikesViewModel()
    @State private var columnVisibility = NavigationSplitViewVisibility.all
        
    var body: some View {
        NavigationSplitView(columnVisibility: $columnVisibility) {
            SideBar(viewModel: viewModel)
                .navigationSplitViewColumnWidth(200)
        } content: {
            if let selectedCountry = $viewModel.selectedCountry {
                ZStack { // workaround for 91311311
                    StationListView(selectedCountry: selectedCountry, viewModel: viewModel)
                }
                .navigationSplitViewColumnWidth(250)
            }
        } detail: {
            if let selectedNetwork = $viewModel.selectedNetwork {
                ZStack { // workaround for 91311311
                    BikeNetworkView(viewModel: viewModel, selectedNtwork: selectedNetwork)
                }
            }
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
    

struct SideBar: View {
    @ObservedObject var viewModel : CityBikesViewModel
    
    var body: some View {
        List(viewModel.countryList, id: \.self, selection: $viewModel.selectedCountry) { countryCode in
            HStack {
                Text(countryFlag(from: countryCode))
                Text(countryName(from: countryCode)).font(.headline)
            }
        }
    }
}

struct StationListView: View {
    @Binding var selectedCountry: String?
    @ObservedObject var viewModel : CityBikesViewModel

    var body: some View {
        if let selectedCountry {
            let networkList = viewModel.getSortedNetworks(countryCode: selectedCountry)!
            
            List(networkList, id: \.self, selection: $viewModel.selectedNetwork) { network in
                HStack {
                    Text("\(network.name) (\(network.city))").font(.subheadline)
                }
            }
            .navigationTitle(countryName(from: selectedCountry))
        }
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
    @ObservedObject var viewModel : CityBikesViewModel
    @Binding var selectedNtwork: Network?
    @State var region = MKCoordinateRegion(center: .init(latitude: 0, longitude: 0),
                                           latitudinalMeters: 5000, longitudinalMeters: 5000)

    var body: some View {
        if let selectedNtwork {
            VStack {
                Text(selectedNtwork.city)
                Map(coordinateRegion: $region,
                    interactionModes: MapInteractionModes.all,
                    showsUserLocation: true,
                    annotationItems: self.viewModel.stationList) { (station) -> MapPin in
                    let coordinate = CLLocationCoordinate2D(latitude: station.latitude,
                                                            longitude: station.longitude)
                    return MapPin(coordinate: coordinate, tint: station.freeBikes() < 5 ? Color.red : Color.green)
                }
            }
            .onAppear(perform: {
                region.center = CLLocationCoordinate2D(latitude: selectedNtwork.latitude,
                                                       longitude: selectedNtwork.longitude)
            })
            .task {
                await viewModel.startObservingBikeShareInfo(network: selectedNtwork.id)
            }
        }
    }
}
