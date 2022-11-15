//
// Created by Himphen on 2022-11-10.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteListView: View {
    @State private var action: Int? = 0
    
    @State var route: TransportRoute
    var etaType: EtaType
    
    var body: some View {
        HStack {
            NavigationLink(
                destination: RouteDetailsView(
                    selectedRoute: route,
                    selectedEtaType: etaType
                ),
                tag: 1, selection: $action
            ) {
            }
            
            HStack(spacing: 0) {
                HStack {
                    Text(route.routeNo)
                    .font(.system(size: 20, weight: .bold))
                }
                .frame(
                    width: 80,
                    alignment: .leading
                )
                .padding(EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 0))
                
                VStack {
                    HStack {
                        Text(MR.strings().add_card_route_to_text.desc().localized())
                        .font(.system(size: 16, weight: .bold))
                        Text(route.getLocalisedDest(context: IOSContext())).font(.system(size: 16, weight: .bold))
                        
                    }
                    .frame(
                        maxWidth: .infinity,
                        alignment: .leading
                    )
                    
                    HStack {
                        Text(MR.strings().add_card_route_from_text.desc().localized())
                        .font(.system(size: 12))
                        Text(route.getLocalisedOrig(context: IOSContext()))
                        .font(.system(size: 12))
                    }
                    .frame(
                        maxWidth: .infinity,
                        alignment: .leading
                    )
                    
                }
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                ).padding(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 10))
                
                if (route.isSpecialRoute()) {
                    VStack {
                        Text(MR.strings().text_add_eta_destination_sp_mobile
                        .formatString(args: [route.serviceType]))
                    }
                    .padding(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 12))
                }
                
            }
            .frame(
                maxWidth: .infinity,
                minHeight: 48,
                alignment: .leading
            )
            .contentShape(Rectangle())
            .onTapGesture {
                self.action = 1
            }
        }
    }
}
