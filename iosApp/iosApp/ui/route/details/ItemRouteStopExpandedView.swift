//
// Created by Himphen on 2022-11-11.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteStopExpandedView: View {
    @State var index: Int
    @State var route: TransportRoute
    @State var routeDetailsStop: RouteDetailsStop
    @State var etaList: [TransportEta]
    @State var viewModel: RouteDetailsVM
    
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
                        maxWidth: 4,
                        maxHeight: .infinity
                    )
                }
                .frame(maxHeight: .infinity)
                .padding(EdgeInsets(top: 0, leading: 12, bottom: 0, trailing: 8))
                
                HStack {
                    if (routeDetailsStop.savedEtaId?.intValue == nil) {
                        Button(action: {
                            viewModel.saveBookmark(position: index)
                        }) {
                            // TODO color
                            HStack {
                                Image("ic_bookmark_add")
                                Text(MR.strings().eta_button_add.desc().localized())
                            }
                            .padding()
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(40)
                        }
                        .padding(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 4))
                    } else {
                        Button(action: {
                            if let savedEtaId = routeDetailsStop.savedEtaId {
                                viewModel.removeBookmark(position: index, savedEtaId: savedEtaId.intValue)
                            }
                        }) {
                            // TODO color
                            HStack {
                                Image("ic_bookmark_remove")
                                .foregroundColor(Color.blue)
                            }
                            .padding()
                            .foregroundColor(.white)
                            .overlay(
                                RoundedRectangle(cornerRadius: 40)
                                .stroke(Color.blue, lineWidth: 2)
                            )
                            .cornerRadius(40)
                        }
                        .padding(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 4))
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .trailing, spacing: 0) {
                        HStack(alignment: .lastTextBaseline, spacing: 0) {
                            if let value = etaList.first?.getEtaMinuteText(default: "-") {
                                Text(String(value.second ?? "-"))
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(MR.colors().eta_card_minutes_text.toColor())
                                
                                if (value.first == true) {
                                    Text(MR.strings().demo_card_eta_minute_classic_unit.desc().localized())
                                    .font(.system(size: 12))
                                }
                            } else {
                                Text("-")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(MR.colors().eta_card_minutes_text.toColor())
                            }
                        }
                        
                        let etaListWithoutFirst = etaListWithoutFirst()
                        
                        if (!etaListWithoutFirst.isEmpty) {
                            HStack(spacing: 0) {
                                ForEach(Array(etaListWithoutFirst.enumerated()), id: \.element) { index, item in
                                    if let value = item.getEtaMinuteText(default: "-") {
                                        Text(String(value.second ?? "-"))
                                        .font(.system(size: 16))
                                        .padding(EdgeInsets(top: 0, leading: 4, bottom: 0, trailing: 0))
                                    }
                                    
                                    if (index == etaListWithoutFirst.count - 1) {
                                        Text(MR.strings().demo_card_eta_minute_classic_unit.desc().localized())
                                        .font(.system(size: 12))
                                    } else {
                                        Text(MR.strings().demo_card_eta_minute_classic_comma.desc().localized())
                                        .font(.system(size: 12))
                                    }
                                }
                            }
                        }
                    }
                }
                .frame(maxHeight: .infinity)
            }
            .fixedSize(horizontal: false, vertical: true)
            .padding(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 12))
        }
        .frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 48,
            alignment: .leading
        )
    }
    
    func etaListWithoutFirst() -> [TransportEta] {
        if (etaList.isEmpty) {
            return etaList
        }
        var etaListWithoutFirst = etaList
        etaListWithoutFirst.removeFirst()
        
        return etaListWithoutFirst
    }
}
