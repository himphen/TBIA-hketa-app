//
// Created by Himphen on 2022-11-21.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import RswiftResources

struct MainView: View {
    @Environment(\.scenePhase) var scenePhase
    
    @StateObject var viewModel: MainVM = MainVM()
    
    @State var selectedTabIndex: Int = 0
    @State var hideBottomMenu = false
    
    @Binding var resetData: Bool
    
    var tabs = MainTabType.allCases.map({ $0.tabItem })
    
    var body: some View {
        NavigationView {
            ZStack {
                VStack(spacing: 0) {
                    if (selectedTabIndex == 0) {
                        BookmarkHomeView()
                    } else if (selectedTabIndex == 2) {
                        SettingsView(resetData: $resetData)
                    }
                    
                    Spacer()
                }
                
                VStack(spacing: 0) {
                    Spacer()
                    MainBottomView(
                        tabBarItems: tabs,
                        selectedTabIndex: $selectedTabIndex
                    )
                }
                .padding(.bottom, 8)
            }
            .navigationBarHidden(true)
            .navigationBarTitle("", displayMode: .inline)
        }
    }
}