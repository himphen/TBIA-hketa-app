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
    
    @StateObject var viewModel: BookmarkHomeVM = BookmarkHomeVM()
    
    @State var etaUpdateTask: Combine.Cancellable? = nil
    @State var etaLastUpdatedTimeTask: Combine.Cancellable? = nil
    
    var body: some View {
        VStack(spacing: 0) {
            if (viewModel.hasData == true) {
                BookmarkHomeListView(
                    viewModel: viewModel
                )
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
            etaRequested(value: true)
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .active {
                Task {
                    await viewModel.getEtaListFromDb()
                }
            } else if newPhase == .inactive {
                etaRequested(value: false)
            }
        }
        .onReceive(viewModel.$etaError) { data in
            if (data != nil) {
                etaRequested(value: false)
                
                Task {
                    do {
                        try await Task.sleep(nanoseconds: 10_000_000_000)
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

struct BookmarkHomeEmptyListView: View {
    @State private var action: Int? = 0
    
    var body: some View {
        VStack(spacing: 0) {
            NavigationLink(
                destination: RouteListView(),
                tag: 1, selection: $action
            ) {
            }
            
            Image("ic_empty_list")
            .resizable() //it will sized so that it fills all the available space
            .aspectRatio(contentMode: .fill)
            .frame(width: 96, height: 96)
            
            Text(MR.strings().empty_eta_list.localized())
            
            Spacer().frame(height: 40)
            Button {
                self.action = 1
            } label: {
                HStack {
                    Image(systemName: "magnifyingglass")
                    Text(MR.strings().eta_button_add.localized())
                }
            }
            .frame(width: 100)
            .buttonStyle(.borderedProminent)
        }
        .frame(maxHeight: .infinity)
    }
}
