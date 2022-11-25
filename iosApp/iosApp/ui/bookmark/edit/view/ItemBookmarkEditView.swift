//
//  ItemBookmarkEditView.swift
//  iosApp
//
//  Created by Himphen on 2022-11-22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ItemBookmarkEditView: View {
    @State var card: Card.SettingsEtaItemCard
    
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
