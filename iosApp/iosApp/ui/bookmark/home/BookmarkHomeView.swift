//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import Combine
import SwiftUI
import shared
import Rswift

struct BookmarkHomeView: View {
    @Environment(\.scenePhase) var scenePhase
    
    @ObservedObject var viewModel: BookmarkHomeVM = BookmarkHomeVM()
    
    @State var etaUpdateTask: Combine.Cancellable? = nil
    @State var etaLastUpdatedTimeTask: Combine.Cancellable? = nil
    
    var body: some View {
        VStack(spacing: 0) {
            if (viewModel.hasData == true) {
                if (viewModel.etaError) {
                    Text(MR.strings().text_eta_loading_failed.desc().localized())
                } else {
                    if let lastUpdatedAgo = viewModel.lastUpdatedAgo {
                        Text(MR.strings().eta_last_updated_at.formatString(
                            context: IOSContext(),
                            args: [lastUpdatedAgo]
                        ))
                    } else {
                        Text(MR.strings().eta_last_updated_at_init.desc().localized())
                    }
                }
                BookmarkHomeListView(
                    viewModel: viewModel
                )
                .onAppear {
                    etaRequested(value: true)
                }
                .onDisappear {
                    etaRequested(value: false)
                }
                .onChange(of: scenePhase) { newPhase in
                    if newPhase == .active {
                        etaRequested(value: true)
                    } else if newPhase == .inactive {
                        etaRequested(value: false)
                    }
                }
                Spacer()
            } else if (viewModel.hasData == false) {
                BookmarkHomeEmptyListView()
            } else {
                Spacer()
            }
        }
        .onAppear { [self] in
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
        .onReceive(viewModel.$etaError) { data in
            if (data) {
                etaRequested(value: false)
                
                Task {
                    do {
                        try await Task.sleep(nanoseconds: 5_000_000_000)
                        etaRequested(value: true)
                    } catch {
                    }
                }
            }
        }
    }
    
    func etaRequested(value: Bool) {
        CommonLoggerUtilsKt.logD(
            message: "etaRequested \(value)"
        )
        
        etaUpdateTask?.cancel()
        etaLastUpdatedTimeTask?.cancel()
        if (value) {
            etaUpdateTask = viewModel.updateEtaList()
            etaLastUpdatedTimeTask = viewModel.updateLastUpdatedTime()
        }
    }
}

struct BookmarkHomeListView: View {
    @ObservedObject var viewModel: BookmarkHomeVM
    
    var body: some View {
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
    }
}

struct BookmarkHomeListActionView: View {
    @State private var action: Int? = 0
    
    var body: some View {
        HStack(spacing: 0) {
            NavigationLink(
                destination: RouteListView(),
                tag: 1, selection: $action
            ) {
            }
            
            NavigationLink(
                destination: RouteListView(),
                tag: 2, selection: $action
            ) {
            }
            
            Button(action: {
                self.action = 1
            }) {
                HStack {
                    Image("ic_search_24")
                    .foregroundColor(Color.blue)
                    Text(MR.strings().eta_button_add.desc().localized())
                    .font(.system(size: 18))
                    .foregroundColor(Color.blue)
                }
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 32)
                    .stroke(Color.gray, lineWidth: 2)
                )
                .cornerRadius(32)
            }
            
            Spacer()
            .frame(width: 8)
            
            Button(action: {
                self.action = 2
            }) {
                HStack {
                    Image("ic_edit_24")
                    .foregroundColor(Color.blue)
                    Text(MR.strings().eta_button_edit.desc().localized())
                    .font(.system(size: 18))
                    .foregroundColor(Color.blue)
                }
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 32)
                    .stroke(Color.gray, lineWidth: 2)
                )
                .cornerRadius(32)
            }
        }
        .padding(12)
    }
}

struct BookmarkHomeEmptyListView: View {
    @State private var action: Int? = 0
    
    var body: some View {
        VStack {
            NavigationLink(
                destination: RouteListView(),
                tag: 1, selection: $action
            ) {
            }
            
            Image("ic_empty_list")
            .resizable() //it will sized so that it fills all the available space
            .aspectRatio(contentMode: .fill)
            .frame(width: 96, height: 96)
            
            Text(MR.strings().empty_eta_list.desc().localized())
            
            Spacer().frame(height: 40)
            Button {
                self.action = 1
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
