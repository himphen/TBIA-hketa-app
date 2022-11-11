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
    
    var selectedStop: TransportStop? = nil
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        self.selectedRoute = selectedRoute
        self.selectedEtaType = selectedEtaType
    }
    
    private var viewModel: RouteDetailsViewModel? = nil
    
    @Published var routeDetailsStopListUpdated: [RouteDetailsStopItem] = []
    
    func activate() async {
        viewModel = RouteDetailsViewModel(
            selectedRoute: selectedRoute,
            selectedEtaType: selectedEtaType,
            routeDetailsStopListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "routeDetailsStopListUpdated")
                
                guard let routeDetailsStopList = viewModel?.routeDetailsStopList else {
                    return
                }
                
                routeDetailsStopListUpdated = routeDetailsStopList.map { item in
                    RouteDetailsStopItem(item: item, isExpanded: false, etaList: [])
                }
            },
            etaListUpdated: { [self] data in
                CommonLoggerUtilsKt.logD(message: "etaListUpdated")
                convertEtaList(etaList: data)
            },
            selectedStopUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "selectedStopUpdated")
                selectedStop = viewModel?.selectedStop
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
    
    func collapsedItem() {
        routeDetailsStopListUpdated = routeDetailsStopListUpdated.enumerated().map { (index, element) in
            RouteDetailsStopItem(
                item: element.item,
                isExpanded: false,
                etaList: []
            )
        }
        viewModel?.selectedStop = nil
    }
    
    func expandedItem(expandedPosition: Int, selectedStop: TransportStop) {
        routeDetailsStopListUpdated = routeDetailsStopListUpdated.enumerated().map { (index, element) in
            RouteDetailsStopItem(
                item: element.item,
                isExpanded: index == expandedPosition,
                etaList: element.etaList
            )
        }
        viewModel?.selectedStop = selectedStop
    }
    
    func updateEtaList() {
        viewModel?.updateEtaList(completionHandler: { _ in
        
        })
    }
    
    func convertEtaList(etaList: [TransportEta]) {
        routeDetailsStopListUpdated = routeDetailsStopListUpdated.map { element in
            if (element.isExpanded) {
                return RouteDetailsStopItem(
                    item: element.item,
                    isExpanded: true,
                    etaList: etaList
                )
            } else {
                return element
            }
        }
    }
}

struct RouteDetailsStopItem: Hashable {
    var item: RouteDetailsStop
    var isExpanded: Bool
    var etaList: [TransportEta]
}