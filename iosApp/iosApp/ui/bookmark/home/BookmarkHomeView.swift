//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct BookmarkHomeView: View {
    @Environment(\.scenePhase) var scenePhase
    
    @ObservedObject var viewModel: BookmarkHomeVM = BookmarkHomeVM()
    
    @State var etaUpdateTimerTask: Task<Void, Error>? = nil
    @State var etaLastUpdatedTimeTimer: Task<Void, Error>? = nil
    
    var body: some View {
        NavigationView {
            VStack {
                if (viewModel.hasData) {
                    if let lastUpdatedAgo = viewModel.lastUpdatedAgo {
                        Text(MR.strings().eta_last_updated_at.formatString(
                            context: IOSContext(),
                            args: [lastUpdatedAgo]
                        ))
                    } else {
                        Text(MR.strings().eta_last_updated_at_init.desc().localized())
                    }
                    BookmarkHomeListView(
                        viewModel: viewModel
                    )
                    .onAppear {
                        etaRequested(value: true)
                        etaLastUpdatedTime(value: true)
                    }
                    .onChange(of: scenePhase) { newPhase in
                        if newPhase == .active {
                            etaRequested(value: true)
                            etaLastUpdatedTime(value: true)
                        } else if newPhase == .inactive {
                            etaRequested(value: false)
                            etaLastUpdatedTime(value: false)
                        } else if newPhase == .background {
                            etaRequested(value: false)
                            etaLastUpdatedTime(value: false)
                        }
                    }
                } else {
                    BookmarkHomeEmptyListView()
                }
            }
        }
        .onAppear {
            Task {
                await viewModel.getEtaListFromDb()
            }
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .active {
                Task {
                    await viewModel.getEtaListFromDb()
                }
            }
        }
    }
    
    func etaRequested(value: Bool) {
        CommonLoggerUtilsKt.logD(
            message: "etaRequested \(value)"
        )
        
        etaUpdateTimerTask?.cancel()
        if (value) {
            etaUpdateTimerTask = viewModel.updateEtaList()
        }
    }
    
    func etaLastUpdatedTime(value: Bool) {
        CommonLoggerUtilsKt.logD(
            message: "etaLastUpdatedTime \(value)"
        )
        
        etaLastUpdatedTimeTimer?.cancel()
        if (value) {
            etaLastUpdatedTimeTimer = viewModel.updateLastUpdatedTime()
        }
    }
}

struct BookmarkHomeListView: View {
    @ObservedObject var viewModel: BookmarkHomeVM
    
    var body: some View {
        List {
            ForEach(viewModel.savedEtaCardList ?? [], id: \.identifier) { item in
                ItemBookmarkHomeView(card: item.item)
                .listRowInsets(EdgeInsets())
            }
        }
        .listStyle(PlainListStyle())
    }
}

struct BookmarkHomeEmptyListView: View {
    var body: some View {
        VStack {
            Image("ic_empty_list")
            .resizable() //it will sized so that it fills all the available space
            .aspectRatio(contentMode: .fill)
            .frame(width: 96, height: 96)
            
            Text(MR.strings().empty_eta_list.desc().localized())
            
            Spacer().frame(height: 40)
            Button {
            } label: {
                HStack {
                    Image(systemName: "magnifyingglass")
                    Text(MR.strings().eta_button_add.desc().localized())
                }
            }
            .frame(width: 100)
            .buttonStyle(.borderedProminent)
        }
    }
}