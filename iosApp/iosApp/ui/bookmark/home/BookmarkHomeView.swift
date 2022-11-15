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
    
    @State var etaUpdateTask: Task<Void, Error>? = nil
    @State var etaLastUpdatedTimeTask: Task<Void, Error>? = nil
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                if (viewModel.hasData == true) {
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
            .navigationBarHidden(true)
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
            ) {}
            .navigationBarTitle("加入", displayMode: .inline)
            
            NavigationLink(
                destination: RouteListView(),
                tag: 2, selection: $action
            ) {}
            .navigationBarTitle("加入", displayMode: .inline)
            
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