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
    
    @State var timer: Timer? = nil
    
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
                        etaRequested(value: false)
                    }
                } else {
                    ItemRouteStopView(route: selectedRoute, routeDetailsStop: item.item)
                    .listRowInsets(EdgeInsets())
                    .onTapGesture {
                        print("meow expand \(index)")
                        viewModel.expandedItem(expandedPosition: index, selectedStop: item.item.transportStop)
                        etaRequested(value: true)
                    }
                }
            }
        }
        .navigationBarTitle(selectedRoute.routeNo + "號線")
        .listStyle(PlainListStyle())
        .task {
            await viewModel.activate()
        }
        .onDisappear {
            timer?.invalidate()
        }
    }
    
    func etaRequested(value: Bool) {
        timer?.invalidate()
        if (value) {
            timer = Timer.scheduledTimer(
                withTimeInterval: 60,
                repeats: true
            ) { [self] (timer) in
                viewModel.updateEtaList()
    
                print("meow expand \(viewModel.selectedStop?.nameTc)")
            }
            timer?.fire()
        }
    }
}