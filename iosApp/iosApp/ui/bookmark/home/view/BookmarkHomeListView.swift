//
//  BookmarkHomeListView.swift
//  iosApp
//
//  Created by Himphen on 2022-11-22.
//  Copyright © 2022 orgName. All rights reserved.
//
import Combine
import SwiftUI
import shared
import Rswift

struct BookmarkHomeListView: View {
    @ObservedObject var viewModel: BookmarkHomeVM
    
    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: 0) {
                if (viewModel.etaError) {
                    Text(MR.strings().text_eta_loading_failed.desc().localized())
                        .foregroundColor(MR.colors().eta_update_failed_text.toColor())
                } else {
                    if let lastUpdatedAgo = viewModel.lastUpdatedAgo {
                        Text(MR.strings().eta_last_updated_at.formatString(
                            context: IOSContext(),
                            args: [lastUpdatedAgo]
                        ))
                        .foregroundColor(MR.colors().eta_update_failed_text.toColor())
                    } else {
                        Text(MR.strings().eta_last_updated_at_init.desc().localized())
                            .foregroundColor(MR.colors().eta_update_failed_text.toColor())
                    }
                }
            }
            .padding(EdgeInsets(top: 8, leading: 24, bottom: 8, trailing: 24))
            .frame(maxWidth: .infinity)
            .background(MR.colors().eta_update_failed_background.toColor())
            
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(viewModel.savedEtaCardList ?? [], id: \.identifier) { item in
                        ItemBookmarkHomeView(card: item.item)
                        Divider()
                    }
                    BookmarkHomeListActionView()
                    Spacer()
                    .frame(height: 100)
                }
            }
            
            Spacer()
        }
        .navigationBarHidden(true)
        .navigationBarTitle("", displayMode: .inline)
        .preferredColorScheme(.dark)
    }
}
