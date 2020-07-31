import SwiftUI
import common


struct ContentView : View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
    
    var body: some View {
        TabView {
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "galway")
                .tabItem {
                    VStack {
                        Image(systemName: "1.circle")
                        Text("Galway")
                    }
                }
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "oslo-bysykkel")
                .tabItem {
                    VStack {
                        Image(systemName: "2.circle")
                        Text("Oslo")
                    }
                }
        }
    }
}

struct StationListView: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    var network: String
 
    var body: some View {
        NavigationView {
            List(cityBikesViewModel.stationList, id: \.id) { station in
                StationView(station: station)
            }
            .navigationBarTitle(Text("Bike Share"))
            .onAppear(perform: {
                self.cityBikesViewModel.fetch(network: self.network)
            })
        }
    }
}

struct StationView : View {
    var station: Station
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(station.name).font(.headline)
            //Text(station.freeBikes ?? "").font(.subheadline)
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
}
