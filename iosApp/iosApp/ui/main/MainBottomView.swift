//
// Created by Himphen on 2022-11-21.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import RswiftResources

struct MainBottomView: View {
    
    let tabBarItems: [MainTabItem]
    var height: CGFloat = 70
    var width: CGFloat = UIScreen.main.bounds.width - 32
    @Binding var selectedTabIndex: Int
    @State var selectedRouteListTab: Bool?
    
    var body: some View {
        HStack {
            NavigationLink(
                destination: RouteListView(),
                tag: true, selection: $selectedRouteListTab
            ) {}
                
            Spacer()
            
            ForEach(tabBarItems.indices) { index in
                let item = tabBarItems[index]
                Button {
                    if (index == 1) {
                        selectedRouteListTab = true
                        return
                    }
                    self.selectedTabIndex = index
                } label: {
                    let isSelected = selectedTabIndex == index
                    MainTabItemView(data: item, isSelected: isSelected)
                }
                Spacer()
            }
        }
        .frame(width: width, height: height)
        .background(Color.white)
        .cornerRadius(13)
        .shadow(radius: 5, x: 0, y: 4)
    }
}
