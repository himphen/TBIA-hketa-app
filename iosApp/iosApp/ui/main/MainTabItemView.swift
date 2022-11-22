//
// Created by Himphen on 2022-11-21.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct MainTabItemView: View {
    let data: MainTabItem
    let isSelected: Bool
    
    var body: some View {
        VStack {
            (isSelected ? data.selectedIcon : data.icon)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: 32, height: 32)
            
            Spacer().frame(height: 4)
            
            Text(data.title)
            .foregroundColor(isSelected ? .black : .gray)
            .font(.system(size: 14))
        }
    }
}

enum MainTabType: Int, CaseIterable {
    case bookmark = 0
    case search = 1
    case settings = 2
    
    var tabItem: MainTabItem {
        switch self {
        case .bookmark:
            return MainTabItem.init(
                icon: R.image.ic_bus_24.image,
                selectedIcon: R.image.ic_bus_24.image,
                title: MR.strings().main_tab_title_bookmark.desc().localized()
            )
        case .settings:
            return MainTabItem.init(
                icon: R.image.ic_settings_24.image,
                selectedIcon: R.image.ic_settings_24.image,
                title: MR.strings().main_tab_title_settings.desc().localized()
            )
        case .search:
            return MainTabItem.init(
                icon: R.image.ic_search_24.image,
                selectedIcon: R.image.ic_search_24.image,
                title: MR.strings().main_tab_title_search.desc().localized()
            )
        }
    }
}

struct MainTabItem {
    var icon: Image
    let selectedIcon: Image
    let title: String
}
