//
//  ContentView.swift
//  CityBikes
//
//  Created by John O'Reilly on 17/11/2019.
//  Copyright © 2019 John O'Reilly. All rights reserved.
//

import SwiftUI
import MapKit
import common


@main
struct BikeShareApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView().environment(\.colorScheme, .dark)
        }
    }
}


struct ContentView : View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
    @State private var selectedLabel: String? = "Galway"
        
    var body: some View {
        NavigationView {
            Sidebar(
                cityBikesViewModel: cityBikesViewModel,
                selectedNetwork: $selectedLabel
            )
            Text("Select label...")
            Text("Select station...")
        }
    }
}


struct Sidebar: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    @Binding var selectedNetwork: String?

    var body: some View {
        List(selection: $selectedNetwork) {
            ForEach(Array(cityBikesViewModel.networkList.keys.sorted(by: { countryName(from: $0) < countryName(from: $1)}) ), id: \.self) { countryCode in
                NavigationLink(destination: StationListView(cityBikesViewModel: cityBikesViewModel,
                                    country: countryName(from: countryCode),
                                    networks: cityBikesViewModel.networkList[countryCode]!))
                {
                    Text(countryName(from: countryCode)).font(.headline)
                }
            }
        }
        .listStyle(SidebarListStyle())
        .onAppear(perform: {
            self.cityBikesViewModel.fetchNetworkList()
        })
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

struct StationListView: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    let country: String
    let networks: [Network]

    var body: some View {
        List(networks.sorted(by: { $0.location.city < $1.location.city }), id: \.id) { network in
            NavigationLink(destination: BikeNetworkView(cityBikesViewModel: cityBikesViewModel,network: network)) {
                Text("\(network.name) (\(network.location.city))").font(.subheadline)
            }

        }
        .navigationTitle(country)
    }
}


extension Station: Identifiable { }

struct BikeNetworkView : View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    var network: Network
    @State var region = MKCoordinateRegion(center: .init(latitude: 0, longitude: 0),
                                           latitudinalMeters: 5000, longitudinalMeters: 5000)

    var body: some View {
        Map(coordinateRegion: $region,
            interactionModes: MapInteractionModes.all,
            showsUserLocation: true,
            annotationItems: self.cityBikesViewModel.stationList) { (station) -> MapPin in
                let coordinate = CLLocationCoordinate2D(latitude: station.latitude,
                                                        longitude: station.longitude)
            return MapPin(coordinate: coordinate, tint: station.freeBikes() < 5 ? Color.red : Color.green)
        }
        .onAppear(perform: {
            region.center = CLLocationCoordinate2D(latitude: network.location.latitude,
                                                   longitude: network.location.longitude)
            self.cityBikesViewModel.fetch(network: network.id)
        })
    }
}
