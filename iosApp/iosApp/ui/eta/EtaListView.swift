//
// Created by Himphen on 2022-11-16.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct EtaListView: View {
    
    @State var etaList: [TransportEta]
    
    var body: some View {
        VStack(alignment: .trailing, spacing: 0) {
            HStack(alignment: .lastTextBaseline, spacing: 0) {
                if let value = etaList.first?.getEtaMinuteText(default: "-") {
                    Text(String(value.second ?? "-"))
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(MR.colors().eta_card_minutes_text.toColor())
                    
                    if (value.first == true) {
                        Text(MR.strings().demo_card_eta_minute_classic_unit.desc().localized())
                        .font(.system(size: 12))
                        .padding(.leading, 2)
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
                            .font(.system(size: 16, weight: .bold))
                            .padding(.leading, 2)
                            .foregroundColor(MR.colors().eta_card_minutes_text.toColor())
                        }
                        
                        if (index == etaListWithoutFirst.count - 1) {
                            Text(MR.strings().demo_card_eta_minute_classic_unit.desc().localized())
                            .font(.system(size: 12))
                            .padding(.leading, 2)
                        } else {
                            Text(MR.strings().demo_card_eta_minute_classic_comma.desc().localized())
                            .font(.system(size: 12))
                            .padding(.leading, 2)
                        }
                    }
                }
            }
        }
    }
    
    func etaListWithoutFirst() -> [TransportEta] {
        let etaList = etaList as! [TransportEta]
        
        if (etaList.isEmpty) {
            return etaList
        }
        var etaListWithoutFirst = etaList
        etaListWithoutFirst.removeFirst()
        
        return etaListWithoutFirst
    }
}
