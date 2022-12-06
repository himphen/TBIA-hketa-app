//
// Created by Himphen on 2022-11-10.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ItemBookmarkHomeView: View {
    @State var card: Card.EtaCard
    
    var body: some View {
        HStack(spacing: 0) {
            let color = card.route.getColor(combineNC: false)
            
            HStack {
                Color(
                    red: (Double(color.red) / 255),
                    green: (Double(color.green) / 255),
                    blue: (Double(color.blue) / 255)
                )
                .frame(maxWidth: 4, maxHeight: .infinity)
                
                Text(card.route.routeNo)
                .font(.system(size: 20, weight: .bold))
            }
            .frame(
                width: 80,
                alignment: .leading
            )
            
            VStack {
                HStack {
                    Text(card.stop.getLocalisedName(context: IOSContext()))
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(MR.colors().common_text_primary.toColor())
                }
                .frame(
                    minWidth: 0,
                    maxWidth: .infinity,
                    alignment: .leading
                )
                
                Spacer()
                .frame(height: 4)
                
                HStack {
                    Text(card.route.getDestDirectionText(context: IOSContext()))
                    .font(.system(size: 16))
                    .foregroundColor(MR.colors().common_text_primary.toColor())
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
            ).padding(EdgeInsets(top: 12, leading: 0, bottom: 12, trailing: 10))
            
            Spacer()
    
            EtaListView(etaList: etaList())
            .frame(
                width: 100,
                alignment: .trailing
            )
        }
        .padding(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 12))
        .frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 48,
            alignment: .leading
        )
    }
    
    func etaList() -> [TransportEta] {
        let etaList = card.etaList as! [TransportEta]
        
        return etaList
    }
}
