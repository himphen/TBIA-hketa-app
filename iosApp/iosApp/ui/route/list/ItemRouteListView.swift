//
// Created by Himphen on 2022-11-10.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteListView: View {
    @State var route: TransportRoute
    @State var etaType: EtaType
    
    var body: some View {
        ZStack {
            HStack {
                let color = route.getColor(combineNC: false)
                
                HStack {
                    Color(
                        red: (Double(color.red) / 255),
                        green: (Double(color.green) / 255),
                        blue: (Double(color.blue) / 255)
                    ).frame(width: 4, height: .infinity)
                    
                    Text(route.routeNo)
                    .font(.system(size: 20, weight: .bold))
                }
                .frame(
                    width: 80,
                    alignment: .leading
                )
                
                VStack {
                    HStack {
                        Text(MR.strings().add_card_route_to_text.desc().localized())
                        .font(.system(size: 16, weight: .bold))
                        Text(route.getLocalisedDest(context: IOSContext())).font(.system(size: 16, weight: .bold))
                        
                    }
                    .frame(
                        minWidth: 0,
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
                        minWidth: 0,
                        maxWidth: .infinity,
                        alignment: .leading
                    )
                    
                }
                .frame(
                    minWidth: 0,
                    maxWidth: .infinity,
                    alignment: .leading
                ).padding(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 10))
                
                if (route.isSpecialRoute()) {
                    VStack {
                        Text(MR.strings().text_add_eta_destination_sp_mobile
                        .formatString(args: [route.serviceType]))
                    }
                }
                
            }
            .frame(
                minWidth: 0,
                maxWidth: .infinity,
                minHeight: 48,
                alignment: .leading
            )
            
            NavigationLink(
                destination: RouteDetailsView(
                    selectedRoute: route,
                    selectedEtaType: etaType
                )
            ) {
                EmptyView()
            }
            .opacity(0.0)
        }
    }
}
