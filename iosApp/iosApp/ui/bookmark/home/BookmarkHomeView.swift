//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct BookmarkHomeView: View {
    @ObservedObject var viewModel: BookmarkHomeVM = BookmarkHomeVM()
    
    var body: some View {
        if (viewModel.hasData) {
        
        } else {
            BookmarkHomeEmptyListView()
        }
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