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
                ).frame(width: 4, height: .infinity)
                
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
                    .font(.system(size: 16, weight: .bold))
                }
                .frame(
                    minWidth: 0,
                    maxWidth: .infinity,
                    alignment: .leading
                )
                
                HStack {
                    Text(card.route.getDestDirectionText(context: IOSContext()))
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
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 0) {
                HStack(alignment: .lastTextBaseline, spacing: 0) {
                    if let value = etaList().first?.getEtaMinuteText(default: "-") {
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
    
    func etaListWithoutFirst() -> [TransportEta] {
        let etaList = card.etaList as! [TransportEta]
        
        if (etaList.isEmpty) {
            return etaList
        }
        var etaListWithoutFirst = etaList
        etaListWithoutFirst.removeFirst()
        
        return etaListWithoutFirst
    }
}
