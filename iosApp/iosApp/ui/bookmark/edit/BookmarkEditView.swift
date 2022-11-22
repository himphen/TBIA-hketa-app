//
//  BookmarkEditView.swift
//  iosApp
//
//  Created by Himphen on 2022-11-22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import SwiftUI
import shared
import Rswift

struct BookmarkEditView: View {
    var body: some View {
        VStack(spacing: 0) {
            List{
                ForEach(["a", "b"], id: \.self) { user in
                    Text(user)
                }
                .onMove(perform: move)
            }
        }
    }
    
    func move(from source: IndexSet, to destination: Int) {
        CommonLoggerUtilsKt.logD(message: "\(source) \(destination)")
    }
}
