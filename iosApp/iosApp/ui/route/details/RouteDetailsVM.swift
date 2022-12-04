//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import Combine
import SwiftUI
import shared
import Rswift
import GoogleMaps

@MainActor class RouteDetailsVM: ObservableObject {
    var selectedRoute: TransportRoute
    var selectedEtaType: EtaType
    
    var selectedStop: TransportStop? = nil
    
    @Published var etaError: String? = nil
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        self.selectedRoute = selectedRoute
        self.selectedEtaType = selectedEtaType
        
        viewModel = RouteDetailsViewModel(
            selectedRoute: selectedRoute,
            selectedEtaType: selectedEtaType,
            routeDetailsStopListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "routeDetailsStopListUpdated")
                
                guard let routeDetailsStopList = viewModel?.routeDetailsStopList else {
                    return
                }
    
                DispatchQueue.main.async { [self] in
                    etaError = nil
                    if (markers.isEmpty) {
                        var bounds = GMSCoordinateBounds()
                        markers = routeDetailsStopList.enumerated().map { (index, routeDetailsStop) in
                            let coordinate2D = CLLocationCoordinate2D(
                                latitude: routeDetailsStop.transportStop.lat,
                                longitude: routeDetailsStop.transportStop.lng
                            )
                            let marker = GMSMarker(position: coordinate2D)
                            marker.userData = RouteDetailsMarkerItem(position: Int32(index))
                            bounds = bounds.includingCoordinate(coordinate2D)
                            let bus = R.image.ic_bus_24()!.withRenderingMode(.alwaysTemplate)
                            let markerView = UIImageView(image: bus)
                            markerView.backgroundColor = selectedRoute.getColor(combineNC: false).toUIColor()
                            markerView.tintColor = .white
                            markerView.layer.cornerRadius = 4
                            markerView.clipsToBounds = true
                            marker.iconView = markerView
                            return marker
                        }
                        self.bounds = bounds
                    }
                    routeDetailsStopListUpdated = routeDetailsStopList.map { routeDetailsStop in
                        RouteDetailsStopItem(item: routeDetailsStop, isExpanded: false, etaList: [])
                    }
                }
            },
            etaListUpdated: { [self] data in
                CommonLoggerUtilsKt.logD(message: "etaListUpdated")
                DispatchQueue.main.async { [self] in
                    etaError = nil
                }
                setEtaListIntoDataList(etaList: data)
            },
            selectedStopUpdated: { [self] in
                CommonLoggerUtilsKt.logD(message: "selectedStopUpdated")
                DispatchQueue.main.async { [self] in
                    etaError = nil
                }
                selectedStop = viewModel?.selectedStop
                
                if let selectedStop = selectedStop {
                    DispatchQueue.main.async { [self] in
                        coordianteForZooming = CLLocationCoordinate2D(
                            latitude: selectedStop.lat,
                            longitude: selectedStop.lng
                        )
                    }
                }
            },
            isSavedEtaBookmarkUpdated: { [self] data1, data2 in
                CommonLoggerUtilsKt.logD(message: "isSavedEtaBookmarkUpdated")
                
                if (data2.intValue > 0) {
                    setSavedBookmark(position: data1.intValue, savedEtaId: data2.intValue)
                }
                
                DispatchQueue.main.async { [self] in
                    showSavedEtaBookmarkToast = true
                }
            },
            isRemovedEtaBookmarkUpdated: { [self] data in
                CommonLoggerUtilsKt.logD(message: "isRemovedEtaBookmarkUpdated")
                
                if (data.intValue > 0) {
                    setSavedBookmark(position: data.intValue, savedEtaId: nil)
                }
            },
            etaUpdateError: { [self] data in
                DispatchQueue.main.async { [self] in
                    etaError = data
                    showEtaErrorToast = true
                }
                CommonLoggerUtilsKt.logD(message: "etaUpdateError")
            }
        )
    }
    
    private var viewModel: RouteDetailsViewModel? = nil
    
    @Published var routeDetailsStopListUpdated: [RouteDetailsStopItem] = []
    @Published var markers: [GMSMarker] = []
    @Published var coordianteForZooming: CLLocationCoordinate2D? = nil
    @Published var bounds: GMSCoordinateBounds? = nil
    @Published var showSavedEtaBookmarkToast: Bool = false
    @Published var showEtaErrorToast: Bool = false
    
    func getRouteDetailsStopList() async {
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
    
    func expandedItem(expandedPosition: Int) {
        let selectedStop = routeDetailsStopListUpdated[expandedPosition].item.transportStop
        
        routeDetailsStopListUpdated = routeDetailsStopListUpdated.enumerated().map { (index, element) in
            RouteDetailsStopItem(
                item: element.item,
                isExpanded: index == expandedPosition,
                etaList: element.etaList
            )
        }
        viewModel?.selectedStop = selectedStop
    }
    
    func updateEtaList() -> Combine.Cancellable {
        DispatchQueue
        .global(qos: .utility)
        .schedule(after: DispatchQueue.SchedulerTimeType(.now()),
            interval: .seconds(60),
            tolerance: .seconds(60 / 5)) { [self] in
            Task {
                CommonLoggerUtilsKt.logD(
                    message: "updateEtaList tickerTask"
                )
                do {
                    try await viewModel?.updateEtaList()
                } catch {
                
                }
            }
        }
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
            } catch {}
        }
    }
    
    func setEtaListIntoDataList(etaList: [TransportEta]) {
        let routeDetailsStopListUpdated = routeDetailsStopListUpdated.map { element in
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
        DispatchQueue.main.async { [self] in
            self.routeDetailsStopListUpdated = routeDetailsStopListUpdated
        }
    }
    
    func setSavedBookmark(position: Int, savedEtaId: Int?) {
        let routeDetailsStopListUpdated = routeDetailsStopListUpdated.enumerated().map { index, element in
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
        DispatchQueue.main.async { [self] in
            self.routeDetailsStopListUpdated = routeDetailsStopListUpdated
        }
    }
}

struct RouteDetailsStopItem: Hashable {
    let identifier = UUID()
    var item: RouteDetailsStop
    var isExpanded: Bool
    var etaList: [TransportEta]
}
