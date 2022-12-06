//
// Created by Himphen on 2022-11-11.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemRouteStopExpandedView: View {
    @State var index: Int
    @State var firstItem: Bool
    @State var lastItem: Bool
    @State var route: TransportRoute
    @State var routeDetailsStop: RouteDetailsStop
    @State var etaList: [TransportEta]
    @State var viewModel: RouteDetailsVM
    
    var body: some View {
        VStack(spacing: 0) {
            ItemRouteStopView(
                firstItem: firstItem,
                lastItem: lastItem,
                route: route,
                routeDetailsStop: routeDetailsStop
            )
            HStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    if (lastItem) {
                        Color.clear.frame(
                            maxWidth: 4,
                            maxHeight: .infinity
                        )
                    } else {
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
                }
                .frame(maxHeight: .infinity)
                .padding(EdgeInsets(top: 0, leading: 12, bottom: 0, trailing: 8))
                
                HStack {
                    if (routeDetailsStop.savedEtaId?.intValue == nil) {
                        Button(action: {
                            viewModel.saveBookmark(position: index)
                        }) {
                            HStack {
                                Image("ic_bookmark_add")
                                Text(MR.strings().eta_button_add.localized())
                            }
                            .padding()
                            .foregroundColor(.white)
                            .background(MR.colors().primary.toColor())
                            .cornerRadius(40)
                        }
                        .padding(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 4))
                    } else {
                        Button(action: {
                            if let savedEtaId = routeDetailsStop.savedEtaId {
                                viewModel.removeBookmark(position: index, savedEtaId: savedEtaId.intValue)
                            }
                        }) {
                            HStack {
                                Image("ic_bookmark_remove")
                                .foregroundColor(MR.colors().primary.toColor())
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
                    
                    EtaListView(etaList: etaList)
                }
                .frame(maxHeight: .infinity)
            }
            .fixedSize(horizontal: false, vertical: true)
        }
        .padding(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 12))
        .frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 48,
            alignment: .leading
        )
    }
}
