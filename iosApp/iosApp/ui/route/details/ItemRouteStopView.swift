//
// Created by Himphen on 2022-11-10.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteStopView: View {
    @State var firstItem: Bool
    @State var lastItem: Bool
    
    @State var route: TransportRoute
    @State var routeDetailsStop: RouteDetailsStop
    
    var body: some View {
        HStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                let color = route.getColor(combineNC: false).toColor()
                if (firstItem) {
                    Color.clear.frame(
                        maxWidth: 4,
                        maxHeight: .infinity
                    )
                } else {
                    color.frame(
                        maxWidth: 4,
                        maxHeight: .infinity
                    )
                }
                color.frame(
                    width: 12,
                    height: 4
                )
                if (lastItem) {
                    Color.clear.frame(
                        maxWidth: 4,
                        maxHeight: .infinity
                    )
                } else {
                    color.frame(
                        maxWidth: 4,
                        maxHeight: .infinity
                    )
                }
                
            }
            .padding(EdgeInsets(top: 0, leading: 12, bottom: 0, trailing: 8))
            
            HStack(alignment: .firstTextBaseline, spacing: 0) {
                Text(String(routeDetailsStop.transportStop.seq?.intValue ?? 0))
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(MR.colors().common_text_secondary.toColor())
                
                Text(routeDetailsStop.transportStop.getLocalisedName(context: IOSContext()))
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
