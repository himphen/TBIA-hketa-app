//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

@MainActor
class RouteDetailsVM: ObservableObject {
    var selectedRoute: TransportRoute
    var selectedEtaType: EtaType
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        self.selectedRoute = selectedRoute
        self.selectedEtaType = selectedEtaType
    }
    
    private var viewModel: RouteDetailsViewModel? = nil
    
    @Published var routeDetailsStopListUpdated: [RouteDetailsStop] = []
    
    func activate() async {
        viewModel = RouteDetailsViewModel(
            selectedRoute: selectedRoute,
            selectedEtaType: selectedEtaType,
            routeDetailsStopListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "routeDetailsStopListUpdated")
                
                guard let routeDetailsStopList = viewModel?.routeDetailsStopList else {
                    return
                }
                
                routeDetailsStopListUpdated = routeDetailsStopList
            },
            etaListUpdated: { [self] data in
                CommonLoggerUtilsKt.logD(message: "etaListUpdated")
            },
            selectedStopUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "selectedStopUpdated")
            },
            isSavedEtaBookmarkUpdated: { [self] data1, data2 in
                CommonLoggerUtilsKt.logD(message: "isSavedEtaBookmarkUpdated")
            },
            isRemovedEtaBookmarkUpdated: { [self] data in
                CommonLoggerUtilsKt.logD(message: "isRemovedEtaBookmarkUpdated")
            },
            etaRequested: { [self] data in
                CommonLoggerUtilsKt.logD(message: "etaRequested")
            },
            etaUpdateError: { [self] data in
                CommonLoggerUtilsKt.logD(message: "etaUpdateError")
            },
            mapMarkerClicked: { [self] data in
                CommonLoggerUtilsKt.logD(message: "mapMarkerClicked")
            },
            onChangedTrafficLayerToggle: { [self] data in
                CommonLoggerUtilsKt.logD(message: "onChangedTrafficLayerToggle")
            }
        )
        
        do {
            try await viewModel?.getRouteDetailsStopList()
        } catch {
        }
    }
}