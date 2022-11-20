//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

@MainActor class RouteDetailsVM: ObservableObject {
    var selectedRoute: TransportRoute
    var selectedEtaType: EtaType
    
    var selectedStop: TransportStop? = nil
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        self.selectedRoute = selectedRoute
        self.selectedEtaType = selectedEtaType
    }
    
    private var viewModel: RouteDetailsViewModel? = nil
    
    @Published var routeDetailsStopListUpdated: [RouteDetailsStopItem] = []
    @Published var showSavedEtaBookmarkToast: Bool = false
    
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
                setEtaListIntoDataList(etaList: data)
            },
            selectedStopUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "selectedStopUpdated")
                selectedStop = viewModel?.selectedStop
            },
            isSavedEtaBookmarkUpdated: { [self] data1, data2 in
                CommonLoggerUtilsKt.logD(message: "isSavedEtaBookmarkUpdated")
                
                if (data2.intValue > 0) {
                    setSavedBookmark(position: data1.intValue, savedEtaId: data2.intValue)
                }
                
                showSavedEtaBookmarkToast = true
            },
            isRemovedEtaBookmarkUpdated: { [self] data in
                CommonLoggerUtilsKt.logD(message: "isRemovedEtaBookmarkUpdated")
                
                if (data.intValue > 0) {
                    setSavedBookmark(position: data.intValue, savedEtaId: nil)
                }
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
    
    func saveBookmark(position: Int) {
        guard let selectedStop = selectedStop else {
            return
        }
        let card = Card.RouteStopAddCard(
            route: selectedRoute,
            stop: selectedStop
        )
        viewModel?.saveBookmark(position: Int32(position), card: card, completionHandler: { _ in
        
        })
    }
    
    func removeBookmark(position: Int, savedEtaId: Int) {
        Task {
            do {
                try await viewModel?.removeBookmark(position: Int32(position), entityId: Int32(savedEtaId))
            } catch {
            }
        }
    }
    
    func setEtaListIntoDataList(etaList: [TransportEta]) {
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
    
    func setSavedBookmark(position: Int, savedEtaId: Int?) {
        routeDetailsStopListUpdated = routeDetailsStopListUpdated.enumerated().map { index, element in
            if (index == position) {
                let newItem = RouteDetailsStopItem(
                    item: element.item,
                    isExpanded: element.isExpanded,
                    etaList: element.etaList
                )
                
                if let savedEtaId = savedEtaId {
                    newItem.item.savedEtaId = KotlinInt(integerLiteral: savedEtaId)
                } else {
                    newItem.item.savedEtaId = nil
                }
                
                return newItem
            } else {
                return element
            }
        }
    }
}

struct RouteDetailsStopItem: Hashable {
    let identifier = UUID()
    var item: RouteDetailsStop
    var isExpanded: Bool
    var etaList: [TransportEta]
}