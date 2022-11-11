//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//
import SwiftUI
import shared
import Rswift

@MainActor
class RouteListVM: ObservableObject {
    private var viewModel: RouteListViewModel? = nil
    
    @Published var filteredTransportRouteList: [TransportRoute] = []
    
    func activate() async {
        viewModel = RouteListViewModel(
            filteredTransportRouteList: { [self] (etaType, list) in
                CommonLoggerUtilsKt.logD(
                    message: "filteredTransportRouteList"
                )
    
                filteredTransportRouteList = list
            },
            tabItemSelectedUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "tabItemSelectedUpdated"
                )
            },
            selectedEtaTypeUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "selectedEtaTypeUpdated"
                )
            }
        )
        
        do {
            try await viewModel?.getTransportRouteList(etaType: EtaType.kmb)
        } catch {
        }
    }
}