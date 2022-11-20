//
// Created by Himphen on 2022-11-21.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct MainView: View {
    @Environment(\.scenePhase) var scenePhase
    
    @State var selectedTabIndex: Int = 0
    
    var tabs = MainTabType.allCases.map({ $0.tabItem })
    
    var body: some View {
        NavigationView {
            ZStack {
                VStack {
                    TabView(selection: $selectedTabIndex) {
                        BookmarkHomeView().tag(0)
                        SettingsView().tag(1)
                    }
                    Spacer()
                }
                
                VStack {
                    Spacer()
                    MainBottomView(
                        tabBarItems: MainTabType.allCases.map({ $0.tabItem }),
                        selectedIndex: $selectedTabIndex
                    )
                }
                .padding(.bottom, 8)
            }
            .navigationBarHidden(true)
            .navigationBarTitle("", displayMode: .inline)
        }
    }
}
