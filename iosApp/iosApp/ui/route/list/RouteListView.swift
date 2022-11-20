//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift
import Combine

struct RouteListView: View {
    @ObservedObject var viewModel: RouteListVM = RouteListVM()
    @State private var selectedTabIndex = 0
    @State private var selectedTab: RouteListTab? = nil
    
    @State var tabs: [RouteListTab] = []
    @State var searchRouteKeyword = ""
    
    init() {
//        UINavigationBar.appearance().titleTextAttributes = [.foregroundColor: UIColor.white]
    }
    
    var body: some View {
        VStack(spacing: 0) {
            SearchBar(text: $searchRouteKeyword)
            GeometryReader { geo in
                VStack(spacing: 0) {
                    // Tabs
                    Tabs(
                        fixed: false,
                        tabs: tabs,
                        geoWidth: geo.size.width,
                        selectedTab: $selectedTabIndex
                    )
                    TabRouteListView(
                        viewModel: viewModel,
                        tab: $selectedTab
                    )
                    .onChange(of: selectedTabIndex, perform: { value in
                        CommonLoggerUtilsKt.logD(
                            message: "onChange selectedTabIndex"
                        )
                        selectedTab = tabs[selectedTabIndex]
                        Task {
                            await viewModel.getTransportRouteList(etaType: selectedTab!.etaType)
                        }
                    })
                    .onChange(of: searchRouteKeyword) { newValue in
                        CommonLoggerUtilsKt.logD(
                            message: "onChange searchRouteKeyword"
                        )
                        viewModel.updateSearchText(searchRouteKeyword: newValue)
                        Task {
                            await viewModel.getTransportRouteList(etaType: selectedTab!.etaType)
                        }
                    }
                }
                .ignoresSafeArea()
            }
        }
        .onAppear { [self] in
            tabs = viewModel.getTabViewData()
            selectedTab = tabs[selectedTabIndex]
            Task {
                await viewModel.getTransportRouteList(etaType: selectedTab!.etaType)
            }
        }
    }
}

struct TabRouteListView: View {
    @ObservedObject var viewModel: RouteListVM
    @Binding var tab: RouteListTab?
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                if let tab {
                    ForEach(viewModel.filteredTransportRouteList[tab.etaType] ?? [], id: \.self) { item in
                        ItemRouteListView(route: item, etaType: tab.etaType)
                        Divider()
                    }
                }
            }
        }
        .navigationBarTitle(
            MR.strings.shared.title_activity_add_eta_with_company_name.formatString(context: IOSContext(), args: [tab?.title ?? ""]),
            displayMode: .inline)
    }
}

struct SearchBar: View {
    @Binding var text: String
    @StateObject var debounceObject = DebounceObject()
    
    @State private var isEditing = false
    
    var body: some View {
        HStack {
            TextField(MR.strings().placeholder_search_route.desc().localized(), text: $debounceObject.text)
            .padding(.vertical, 8)
            .padding(.horizontal, 12)
            .background(Color(.systemGray6))
            .cornerRadius(22)
            .onTapGesture {
                self.isEditing = true
            }
            .onChange(of: debounceObject.debouncedText) { newText in
                text = newText
            }
            
            if isEditing {
                Button(action: {
                    self.isEditing = false
                    self.text = ""
                }) {
                    Text(MR.strings().dialog_cancel_btn.desc().localized())
                }
                .padding(.trailing, 10)
                .transition(.move(edge: .trailing))
                .animation(.default)
            }
        }
    }
}

public final class DebounceObject: ObservableObject {
    @Published var text: String = ""
    @Published var debouncedText: String = ""
    private var bag = Set<AnyCancellable>()
    
    public init(dueTime: TimeInterval = 0.5) {
        $text
        .removeDuplicates()
        .debounce(for: .seconds(dueTime), scheduler: DispatchQueue.main)
        .sink(receiveValue: { [weak self] value in
            self?.debouncedText = value
        })
        .store(in: &bag)
    }
}