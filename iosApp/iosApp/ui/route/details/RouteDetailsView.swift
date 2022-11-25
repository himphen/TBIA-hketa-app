//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift
import AlertToast

struct RouteDetailsView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State var selectedRoute: TransportRoute
    @State var selectedEtaType: EtaType
    @StateObject var viewModel: RouteDetailsVM
    
    @State var etaUpdateTimer: Timer? = nil
    
    init(selectedRoute: TransportRoute, selectedEtaType: EtaType) {
        _selectedRoute = State(initialValue: selectedRoute)
        _selectedEtaType = State(initialValue: selectedEtaType)
        
        _viewModel = StateObject(wrappedValue: RouteDetailsVM(
            selectedRoute: selectedRoute,
            selectedEtaType: selectedEtaType
        ))
    }
    
    var body: some View {
        VStack {
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
                                print("meow collapse \(index)")
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
                                print("meow expand \(index)")
                                viewModel.expandedItem(expandedPosition: index, selectedStop: item.item.transportStop)
                                etaRequested(value: true)
                            }
                        }
                    }
                }
            }
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
        .task {
            await viewModel.activate()
        }
        .onDisappear {
            etaUpdateTimer?.invalidate()
        }
        .toast(isPresenting: $viewModel.showSavedEtaBookmarkToast, duration: 3, alert: {
            AlertToast(displayMode: .banner(.slide), type: .regular, title: "Done")
        }, completion: {
            viewModel.showSavedEtaBookmarkToast = false
        })
    }
    
    func etaRequested(value: Bool) {
        etaUpdateTimer?.invalidate()
        if (value) {
            etaUpdateTimer = Timer.scheduledTimer(
                withTimeInterval: 60,
                repeats: true
            ) { [self] (timer) in
                viewModel.updateEtaList()
                
                print("meow expand \(viewModel.selectedStop?.nameTc)")
            }
            etaUpdateTimer?.fire()
        }
    }
    
    private func dismiss() {
        presentationMode.wrappedValue.dismiss()
    }
}
