//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct RouteListView: View {
    @ObservedObject var viewModel: RouteListVM = RouteListVM()
    @State private var selectedTabIndex = 0
    
    init() {
//        UINavigationBar.appearance().titleTextAttributes = [.foregroundColor: UIColor.white]
    }
    
    var body: some View {
        let tabs = viewModel.getTabViewData()
        VStack(spacing: 0) {
            SearchBar(text: .constant(""))
            GeometryReader { geo in
                VStack(spacing: 0) {
                    // Tabs
                    Tabs(
                        fixed: false,
                        tabs: tabs,
                        geoWidth: geo.size.width,
                        selectedTab: $selectedTabIndex
                    )
                    
                    // Views
                    TabView(
                        selection: $selectedTabIndex,
                        content: {
                            ForEach(Array(tabs.enumerated()), id: \.element.identifier) { index, item in
                                TabRouteListView(
                                    tab: item,
                                    routeList: viewModel.filteredTransportRouteList[tabs[index].etaType] ?? []
                                )
                                .tag(index)
                            }
                        }
                    )
                    .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                    .onChange(of: selectedTabIndex, perform: { value in
                        CommonLoggerUtilsKt.logD(
                            message: "onChange"
                        )
                        Task {
                            await viewModel.getTransportRouteList(etaType: tabs[selectedTabIndex].etaType)
                        }
                    })
                    .onAppear {
                        Task {
                            await viewModel.getTransportRouteList(etaType: tabs[0].etaType)
                        }
                    }
                }
                .ignoresSafeArea()
            }
        }
    }
}

struct TabRouteListView: View {
    @State var tab: Tab
    @State var routeList: [TransportRoute]
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                ForEach(routeList, id: \.self) { item in
                    ItemRouteListView(route: item, etaType: tab.etaType)
                    Divider()
                }
            }
        }
        .navigationBarTitle(
            MR.strings.shared.title_activity_add_eta_with_company_name.formatString(context: IOSContext(), args: [tab.title]),
            displayMode: .inline)
    }
}

struct SearchBar: View {
    @Binding var text: String
    
    @State private var isEditing = false
    
    var body: some View {
        HStack {
            TextField("Search ...", text: $text)
            .padding(7)
            .padding(.horizontal, 25)
            .background(Color(.systemGray6))
            .cornerRadius(8)
            .padding(.horizontal, 10)
            .onTapGesture {
                self.isEditing = true
            }
            
            if isEditing {
                Button(action: {
                    self.isEditing = false
                    self.text = ""
                    
                }) {
                    Text("Cancel")
                }
                .padding(.trailing, 10)
                .transition(.move(edge: .trailing))
                .animation(.default)
            }
        }
    }
}