//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct RouteDetailsView: View {
    @State var selectedRoute: TransportRoute
    @State var selectedEtaType: EtaType
    @ObservedObject var viewModel: RouteDetailsVM
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        self.selectedRoute = selectedRoute
        self.selectedEtaType = selectedEtaType
        
        viewModel = RouteDetailsVM(
            selectedRoute: selectedRoute,
            selectedEtaType: selectedEtaType
        )
    }
    
    var body: some View {
        List(viewModel.routeDetailsStopListUpdated, id: \.self) { item in
            ItemRouteStopView(route: selectedRoute, routeDetailsStop: item)
            .listRowInsets(EdgeInsets())
        }
        .navigationBarTitle(selectedRoute.routeNo + "號線")
        .listStyle(PlainListStyle())
        .task {
            await viewModel.activate()
        }
    }
}