//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import Combine
import SwiftUI
import shared
import RswiftResources
import AlertToast
import GoogleMaps

struct RouteDetailsView: View {
    @Environment(\.scenePhase) var scenePhase
    
    @Environment(\.presentationMode) var presentationMode
    
    @State var selectedRoute: TransportRoute
    @State var selectedEtaType: EtaType
    @StateObject var viewModel: RouteDetailsVM
    
    @State var etaUpdateTask: Combine.Cancellable? = nil
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        _selectedRoute = State(initialValue: selectedRoute)
        _selectedEtaType = State(initialValue: selectedEtaType)
        
        _viewModel = StateObject(wrappedValue: RouteDetailsVM(
            selectedRoute: selectedRoute,
            selectedEtaType: selectedEtaType
        ))
    }
    
    var body: some View {
        GeometryReader { gp in
            VStack {
                GoogleMapsView(
                    markers: $viewModel.markers,
                    bounds: $viewModel.bounds,
                    coordianteForZooming: $viewModel.coordianteForZooming,
                    mapMarkerClicked: { [self] item in
                        viewModel.expandedItem(expandedPosition: Int(item.position))
                        etaRequested(value: true)
                    }
                )
                .frame(width: gp.size.width, height: gp.size.height * 0.3)
                .frame(minWidth: 0, maxWidth: .infinity)
                
                ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(Array(viewModel.routeDetailsStopListUpdated.enumerated()), id: \.element.identifier) { index, item in
                            if (item.isExpanded) {
                                ItemRouteStopExpandedView(
                                    index: index,
                                    firstItem: (index == viewModel.routeDetailsStopListUpdated.startIndex),
                                    lastItem: (index == viewModel.routeDetailsStopListUpdated.endIndex - 1),
                                    route: selectedRoute,
                                    routeDetailsStop: item.item,
                                    etaList: item.etaList,
                                    viewModel: viewModel
                                )
                                .frame(
                                    maxWidth: .infinity
                                )
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    viewModel.collapsedItem()
                                    etaRequested(value: false)
                                }
                            } else {
                                ItemRouteStopView(
                                    firstItem: (index == viewModel.routeDetailsStopListUpdated.startIndex),
                                    lastItem: (index == viewModel.routeDetailsStopListUpdated.endIndex - 1),
                                    route: selectedRoute,
                                    routeDetailsStop: item.item
                                )
                                .frame(
                                    maxWidth: .infinity
                                )
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    viewModel.expandedItem(expandedPosition: index)
                                    etaRequested(value: true)
                                }
                            }
                        }
                    }
                }
                .frame(width: gp.size.width, height: gp.size.height * 0.7)
                .frame(minWidth: 0, maxWidth: .infinity)
            }
            .navigationBarTitle(
                selectedRoute.routeNo + " - " + selectedRoute.getDirectionSubtitleText(context: IOSContext()),
                displayMode: .inline
            )
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        dismiss()
                    } label: {
                        HStack {
                            Image(systemName: "chevron.left")
                        }
                    }
                }
            }
            .onAppear { [self] in
                Task {
                    await viewModel.getRouteDetailsStopList()
                }
            }
            .onChange(of: scenePhase) { newPhase in
                if newPhase == .active {
                    etaRequested(value: true)
                } else if newPhase == .inactive {
                    etaRequested(value: false)
                }
            }
            .toast(isPresenting: $viewModel.showSavedEtaBookmarkToast, duration: 5, alert: {
                AlertToast(displayMode: .banner(.slide), type: .regular, title: MR.strings().toast_eta_added.localized())
            }, completion: {
                viewModel.showSavedEtaBookmarkToast = false
            })
            .toast(isPresenting: $viewModel.showEtaErrorToast, duration: 5, alert: {
                AlertToast(displayMode: .banner(.slide), type: .regular, title: viewModel.etaError, style:
                            AlertToast.AlertStyle.style(backgroundColor: MR.colors().secondary_yellow_light.toColor()))
            }, completion: {
                viewModel.showEtaErrorToast = false
            })
            .onReceive(viewModel.$etaError) { data in
                if (data != nil) {
                    etaRequested(value: false)
                    
                    Task {
                        do {
                            try await Task.sleep(nanoseconds: 10_000_000_000)
                            etaRequested(value: true)
                        } catch {
                        }
                    }
                }
            }
        }
    }
    
    func etaRequested(value: Bool) {
        CommonLoggerUtilsKt.logD(
            message: "etaRequested \(value)"
        )
        
        etaUpdateTask?.cancel()
        if (value) {
            etaUpdateTask = viewModel.updateEtaList()
        }
    }
    
    private func dismiss() {
        presentationMode.wrappedValue.dismiss()
    }
}
