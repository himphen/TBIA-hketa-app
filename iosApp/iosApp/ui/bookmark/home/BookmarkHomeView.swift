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
        VStack {
            Text("Welcome").task({
                await viewModel.activate()
            })
        }
    }
}
