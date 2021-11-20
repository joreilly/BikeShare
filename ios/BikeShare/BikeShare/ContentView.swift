import SwiftUI
import common


struct ContentView : View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
    @State private var selection = 0
    
    var body: some View {
        TabView {
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "galway")
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Galway")
                    }
                }.tag(0)
            NetworkListView(cityBikesViewModel: cityBikesViewModel)
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Networks")
                    }
                }.tag(1)

            }
    }
}

struct StationListView: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    var network: String
 
    var body: some View {
        List(cityBikesViewModel.stationList, id: \.id) { station in
            StationView(station: station)
        }
        .navigationBarTitle(Text("Bike Stations"))
        .onAppear {
            cityBikesViewModel.startObservingBikeShareInfo(network: network)
        }
        .onDisappear {
            cityBikesViewModel.stopObservingBikeShareInfo()
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
    }
}


struct NetworkListView : View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel

    var body: some View {
        List(cityBikesViewModel.networkList, id: \.id) { network in
            Text(network.name + " (" + network.location.city + ")")
        }
        .navigationBarTitle(Text("Networks"))
//        .onAppear {
//            cityBikesViewModel.fetchNetworks()
//        }
    }
}
