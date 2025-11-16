import SwiftUI
import MapKit
import BikeShareKit
import KMPObservableViewModelCore
import KMPObservableViewModelSwiftUI

// BikeShare theme colors matching Compose
let BikeGreen = Color(red: 0x1E/255.0, green: 0x8E/255.0, blue: 0x3E/255.0)
let BikeGreenLight = Color(red: 0x7C/255.0, green: 0xB3/255.0, blue: 0x42/255.0)
let BikeGreenDark = Color(red: 0x00/255.0, green: 0x5D/255.0, blue: 0x1B/255.0)
let BikeBlue = Color(red: 0x19/255.0, green: 0x76/255.0, blue: 0xD2/255.0)
let BikeBlueDark = Color(red: 0x0D/255.0, green: 0x47/255.0, blue: 0xA1/255.0)

// Availability colors matching Compose
let lowAvailabilityColor = Color(red: 0xE6/255.0, green: 0x51/255.0, blue: 0x00/255.0)
let mediumAvailabilityColor = Color(red: 0xFF/255.0, green: 0xA0/255.0, blue: 0x00/255.0)
let highAvailabilityColor = Color(red: 0x2E/255.0, green: 0x7D/255.0, blue: 0x32/255.0)

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
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(viewModel.countryList.filter { query.isEmpty || $0.displayName.contains(query)}, id: \.self) { country in
                        NavigationLink(destination: NetworkListView(applicationComponent: applicationComponent, countryCode: country.code)) {
                            CountryCardView(country: country)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
            }
            .background(Color(UIColor.systemGroupedBackground))
            .searchable(text: $query)
            .navigationTitle("BikeShare")
            .navigationBarTitleDisplayMode(.large)
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct CountryCardView: View {
    let country: Country
    
    var body: some View {
        HStack(alignment: .center, spacing: 16) {
            // Flag
            Text(countryFlag(from: country.code))
                .font(.system(size: 40))
                .frame(width: 48, height: 48)
                .background(Color(UIColor.secondarySystemGroupedBackground))
                .cornerRadius(4)
                .padding(4)
            
            // Country name
            Text(country.displayName)
                .font(.body)
                .fontWeight(.medium)
                .foregroundColor(.primary)
            
            Spacer()
        }
        .padding(16)
        .frame(maxWidth: .infinity)
        .background(Color(UIColor.systemBackground))
        .cornerRadius(8)
        .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
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
        ScrollView {
            LazyVStack(spacing: 8) {
                ForEach(viewModel.networkList) { network in
                    NavigationLink(destination: StationListTabView(applicationComponent: applicationComponent, network: network)) {
                        NetworkCardView(network: network)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
        }
        .background(Color(UIColor.systemGroupedBackground))
        .navigationTitle(countryName(from: countryCode))
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            viewModel.setCountryCode(countryCode: countryCode)
        }
    }
}

struct NetworkCardView: View {
    let network: Network
    
    var body: some View {
        HStack(alignment: .center, spacing: 16) {
            // Color indicator bar
            RoundedRectangle(cornerRadius: 4)
                .fill(BikeGreen.opacity(0.7))
                .frame(width: 8, height: 40)
            
            // Network info
            VStack(alignment: .leading, spacing: 4) {
                Text(network.city)
                    .font(.body)
                    .fontWeight(.bold)
                    .foregroundColor(.primary)
                    .lineLimit(1)
                
                Text(network.name)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
            }
            
            Spacer()
        }
        .padding(16)
        .frame(maxWidth: .infinity)
        .background(Color(UIColor.systemBackground))
        .cornerRadius(8)
        .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
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
        ScrollView {
            LazyVStack(spacing: 8) {
                ForEach(stations.sorted { $0.name < $1.name }) { station in
                    StationView(station: station)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
        }
        .background(Color(UIColor.systemGroupedBackground))
        .navigationTitle(network.name)
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct StationView : View {
    var station: Station

    var body: some View {
        let totalSlots = station.freeBikes() + station.slots()
        let bikePercentage = totalSlots > 0 ? Float(station.freeBikes()) / Float(totalSlots) : 0.0
        
        // Determine availability color based on percentage of available bikes
        let availabilityColor: Color = {
            if bikePercentage < 0.2 {
                return lowAvailabilityColor
            } else if bikePercentage < 0.5 {
                return mediumAvailabilityColor
            } else {
                return highAvailabilityColor
            }
        }()
        
        VStack(alignment: .leading, spacing: 12) {
            // Station name
            Text(station.name)
                .font(.headline)
                .fontWeight(.bold)
                .lineLimit(2)
            
            // Availability progress indicator
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color(UIColor.secondarySystemGroupedBackground))
                        .frame(height: 8)
                    
                    RoundedRectangle(cornerRadius: 4)
                        .fill(availabilityColor)
                        .frame(width: geometry.size.width * CGFloat(bikePercentage), height: 8)
                }
            }
            .frame(height: 8)
            
            // Availability details
            HStack(spacing: 0) {
                // Bikes available
                HStack(spacing: 8) {
                    ZStack {
                        Circle()
                            .fill(availabilityColor.opacity(0.15))
                            .frame(width: 32, height: 32)
                        
                        Image(systemName: "bicycle")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(availabilityColor)
                    }
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Available Bikes")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text("\(station.freeBikes())")
                            .font(.body)
                            .fontWeight(.bold)
                            .foregroundColor(availabilityColor)
                    }
                }
                
                Spacer()
                
                // Empty slots
                HStack(spacing: 8) {
                    ZStack {
                        Circle()
                            .fill(BikeBlue.opacity(0.15))
                            .frame(width: 32, height: 32)
                        
                        Image(systemName: "parkingsign")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(BikeBlue)
                    }
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Empty Slots")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text("\(station.slots())")
                            .font(.body)
                            .fontWeight(.bold)
                            .foregroundColor(BikeBlue)
                    }
                }
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(UIColor.systemBackground))
        .cornerRadius(8)
        .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
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
                .font(.headline)
                .padding(.top, 8)
            Map(coordinateRegion: $region,
                interactionModes: MapInteractionModes.all,
                showsUserLocation: true,
                annotationItems: stations) { (station) -> MapPin in
                    let coordinate = CLLocationCoordinate2D(latitude: station.latitude,
                                                            longitude: station.longitude)
                    let totalSlots = station.freeBikes() + station.slots()
                    let bikePercentage = totalSlots > 0 ? Float(station.freeBikes()) / Float(totalSlots) : 0.0
                    
                    // Use availability colors matching Compose
                    let pinColor: Color = {
                        if bikePercentage < 0.2 {
                            return lowAvailabilityColor
                        } else if bikePercentage < 0.5 {
                            return mediumAvailabilityColor
                        } else {
                            return highAvailabilityColor
                        }
                    }()
                    
                return MapPin(coordinate: coordinate, tint: pinColor)
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
                                   
