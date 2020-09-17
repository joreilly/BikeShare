import SwiftUI
import common


struct ContentView : View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
    @State private var selection = 0
    
    var body: some View {
        TabView(selection: $selection) {
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "galway")
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Galway")
                    }
                }.tag(0)
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "oslo-bysykkel")
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Oslo")
                    }
                }.tag(1)
        }
    }
}

struct StationListView: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    var network: String
    let timer = Timer.publish(every: 30, on: .main, in: .common).autoconnect()
 
    var body: some View {
        NavigationView {
            List(cityBikesViewModel.stationList, id: \.id) { station in
                StationView(station: station)
            }
            .navigationBarTitle(Text("Bike Share"))
            .onReceive(timer) { _ in
                self.cityBikesViewModel.fetch(network: self.network)
            }
            .onAppear(perform: {
                self.cityBikesViewModel.fetch(network: self.network)
            })
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
