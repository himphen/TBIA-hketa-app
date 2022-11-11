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
        List {
            ForEach(Array(viewModel.routeDetailsStopListUpdated.enumerated()), id: \.element) { index, item in
                if (item.isExpanded) {
                    ItemRouteStopExpandedView(
                        route: selectedRoute,
                        routeDetailsStop: item.item,
                        etaList: item.etaList
                    )
                    .listRowInsets(EdgeInsets())
                    .onTapGesture {
                        print("meow collapse \(index)")
                        viewModel.collapsedItem()
                    }
                } else {
                    ItemRouteStopView(route: selectedRoute, routeDetailsStop: item.item)
                    .listRowInsets(EdgeInsets())
                    .onTapGesture {
                        print("meow expand \(index)")
                        viewModel.expandedItem(expandedPosition: index, selectedStop: item.item.transportStop)
                    }
                }
            }
        }
        .navigationBarTitle(selectedRoute.routeNo + "號線")
        .listStyle(PlainListStyle())
        .task {
            await viewModel.activate()
        }
    }
}