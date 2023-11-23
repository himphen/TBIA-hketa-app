//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import RswiftResources

@MainActor class RouteListVM: ObservableObject {
    private var viewModel: RouteListViewModel? = nil
    
    @Published var filteredTransportRouteList = [EtaType: [TransportRoute]]()
    
    let list: [EtaType] = [
        EtaType.kmb,
        EtaType.ctb,
        EtaType.nlb,
        EtaType.gmbHki,
        EtaType.gmbKln,
        EtaType.gmbNt
    ]
    
    init() {
        viewModel = RouteListViewModel(
            filteredTransportRouteList: { [self] (etaType, list) in
                CommonLoggerUtilsKt.logD(
                    message: "filteredTransportRouteList"
                )
                
                DispatchQueue.main.async { [self] in
                    filteredTransportRouteList[etaType] = list
                }
            }
        )
    }
    
    func updateSearchText(searchRouteKeyword: String) {
        CommonLoggerUtilsKt.logD(
            message: "updateSearchText"
        )
        
        viewModel?.searchRouteKeyword = searchRouteKeyword
    }
    
    func getTransportRouteList(etaType: EtaType) async {
        do {
            try await viewModel?.getTransportRouteList(etaType: etaType)
        } catch {
            CommonLoggerUtilsKt.logD(
                message: error.localizedDescription
            )
        }
    }
    
    func getTabViewData() -> [RouteListTab] {
        list.map { element in
            var image: Image
            switch (element) {
            case EtaType.kmb:
                image = R.image.ic_bus_24.image
                break
            default:
                image = R.image.ic_bus_24.image
            }
            
            let color = element.color()
            
            return RouteListTab.init(
                icon: image,
                title: element.name().localized(),
                color: Color(
                    red: (Double(color.red) / 255),
                    green: (Double(color.green) / 255),
                    blue: (Double(color.blue) / 255)
                ),
                etaType: element
            )
        }
        
        
    }
}
