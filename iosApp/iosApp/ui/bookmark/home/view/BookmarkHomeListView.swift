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
import RswiftResources

struct BookmarkHomeListView: View {
    @Environment(\.scenePhase) var scenePhase
    @State var selectedAppearance = 1
    
    @StateObject var viewModel: BookmarkHomeVM
    
    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: 0) {
                if let etaError = viewModel.etaError {
                    Text(etaError)
                    .foregroundColor(MR.colors().eta_update_failed_text.toColor())
                } else {
                    if let lastUpdatedAgo = viewModel.lastUpdatedAgo {
                        Text(MR.strings().eta_last_updated_at.localized(
                            args: [lastUpdatedAgo]
                        ))
                        .foregroundColor(MR.colors().eta_update_failed_text.toColor())
                    } else {
                        Text(MR.strings().eta_last_updated_at_init.localized())
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
                    BookmarkHomeListActionView(selectedAppearance: $selectedAppearance)
                    Spacer()
                    .frame(height: 100)
                }
            }
            
            Spacer()
        }
        .onAppear { [self] in
            self.selectedAppearance = 2
        }
        .onDisappear { [self] in
            self.selectedAppearance = 1
        }
        .navigationBarHidden(true)
        .navigationBarTitle("", displayMode: .inline)
        .preferredColorScheme(selectedAppearance == 1 ? .light : selectedAppearance == 2 ? .dark : nil)
        
    }
}
