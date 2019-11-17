//
//  ContentView.swift
//  CityBikes
//
//  Created by John O'Reilly on 17/11/2019.
//  Copyright © 2019 John O'Reilly. All rights reserved.
//

import SwiftUI
import common

struct ContentView: View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
 
    var body: some View {
        List(cityBikesViewModel.stationList, id: \.id) { station in
            StationView(station: station)
        }
        .navigationBarTitle(Text("Routes"), displayMode: .large)
        .onAppear(perform: cityBikesViewModel.fetch)
    }
}


struct StationView : View {
    var station: Station
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(station.name).font(.headline)
            }
        }
    }
}
