//
// Created by Himphen on 2022-11-11.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteStopExpandedView: View {
    @State var route: TransportRoute
    @State var routeDetailsStop: RouteDetailsStop
    @State var etaList: [TransportEta]
    
    var body: some View {
        VStack(spacing: 0) {
            ItemRouteStopView(route: route, routeDetailsStop: routeDetailsStop)
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
                }
                .padding(EdgeInsets(top: 0, leading: 12, bottom: 0, trailing: 8))
                
                Button(action: {
                    print("Hello World tapped!")
                }) {
                    R.image.ic_streetview_24.image
                }
                
                VStack(spacing: 0) {
                    HStack(spacing: 0) {
                        if let value = etaList.first?.getEtaMinuteText(default: "-") {
                            Text(String(value.second ?? "-"))
                            .font(.system(size: 24, weight: .bold))
//                            .foregroundColor(color: MR.colors().eta_card_minutes_text)
                            
                            if (value.first == true) {
                                Text(MR.strings().demo_card_eta_minute_classic_unit.desc().localized())
                                .font(.system(size: 12))
                            }
                        } else {
                            Text("-")
                            .font(.system(size: 24, weight: .bold))
//                            .foregroundColor(color: MR.colors.eta_card_minutes_text)
                        }
                    }
                    
                    ScrollView(.horizontal) {
                        HStack {
                            ForEach(Array(etaList.enumerated()), id: \.element) { index, item in
                                if let value = item.getEtaMinuteText(default: "-") {
                                    Text(String(value.second ?? "-"))
                                }
                                
                                if (index == etaList.count - 1) {
                                    Text(MR.strings().demo_card_eta_minute_classic_unit.desc().localized())
                                    .font(.system(size: 12))
                                } else {
                                    Text(MR.strings().demo_card_eta_minute_classic_comma.desc().localized())
                                    .font(.system(size: 12))
                                }
                                Spacer()
                            }
                        }
                    }
                }
                
            }
        }
        .frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 48,
            alignment: .leading
        )
    }
}
