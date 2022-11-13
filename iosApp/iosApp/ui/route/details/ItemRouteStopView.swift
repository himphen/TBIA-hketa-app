//
// Created by Himphen on 2022-11-10.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteStopView: View {
    @State var route: TransportRoute
    @State var routeDetailsStop: RouteDetailsStop
    
    var body: some View {
        HStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                let color = route.getColor(combineNC: false)
                Color(
                    red: (Double(color.red) / 255),
                    green: (Double(color.green) / 255),
                    blue: (Double(color.blue) / 255)
                ).frame(
                    width: 4,
                    height: .infinity
                )
                Color(
                    red: (Double(color.red) / 255),
                    green: (Double(color.green) / 255),
                    blue: (Double(color.blue) / 255)
                ).frame(
                    width: 12,
                    height: 4
                )
                Color(
                    red: (Double(color.red) / 255),
                    green: (Double(color.green) / 255),
                    blue: (Double(color.blue) / 255)
                ).frame(
                    width: 4,
                    height: .infinity
                )
            }
            .padding(EdgeInsets(top: 0, leading: 12, bottom: 0, trailing: 8))
            
            HStack(alignment: .lastTextBaseline, spacing: 0) {
                Text(String(routeDetailsStop.transportStop.seq?.intValue ?? 0))
                .font(.system(size: 12, weight: .bold))
                // TODO color
                .foregroundColor(.gray)
                
                Text(routeDetailsStop.transportStop.nameTc)
                .font(.system(size: 16, weight: .bold))
                .padding(EdgeInsets(top: 0, leading: 8, bottom: 0, trailing: 0))
            }
            .padding(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 10))
            
        }
        .frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 48,
            alignment: .leading
        )
    }
}
