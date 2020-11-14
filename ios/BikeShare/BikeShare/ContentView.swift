import SwiftUI
import SwiftUIRefresh
import common


struct ContentView : View {
    @ObservedObject var cityBikesViewModel = CityBikesViewModel(repository: CityBikesRepository())
    @State private var selection = 0
    
    var body: some View {
        TabView(selection: $selection) {
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "galway", tag: 0, selection: $selection)
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Galway")
                    }
                }.tag(0)
            StationListView(cityBikesViewModel: cityBikesViewModel, network: "oslo-bysykkel", tag: 1, selection: $selection)
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Oslo")
                    }
                }.tag(1)
            NetworkListView(cityBikesViewModel: cityBikesViewModel)
                .tabItem {
                    VStack {
                        Image(systemName: "location")
                        Text("Networks")
                    }
                }.tag(2)

            }
        
    }
}

// see https://stackoverflow.com/questions/63978584/tabview-lifecycle-issue-if-views-are-the-same/63981763#63981763
struct StationListView: View {
    @ObservedObject var cityBikesViewModel : CityBikesViewModel
    var network: String
    var tag: Int
    @Binding var selection: Int
    @State private var isShowing = false
 
    var body: some View {
        NavigationView {
            List(cityBikesViewModel.stationList, id: \.id) { station in
                StationView(station: station)
            }
            .navigationBarTitle(Text("Bike Share"))
            .onChange(of: selection) { _ in
                refreshData()
            }
            .onAppear {
                refreshData()
            }
            .pullToRefresh(isShowing: $isShowing) {
                refreshData()
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.isShowing = false
                }
            }
        }
    }
    
    func refreshData() {
        if tag == selection {
            cityBikesViewModel.fetchBikeShareInfo(network: self.network)
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
        NavigationView {
            List(cityBikesViewModel.networkList, id: \.id) { network in
                Text(network.name + " (" + network.location.city + ")")
            }
            .navigationBarTitle(Text("Networks"))
            .onAppear {
                cityBikesViewModel.fetchNetworks()
            }
        }
    }
}
