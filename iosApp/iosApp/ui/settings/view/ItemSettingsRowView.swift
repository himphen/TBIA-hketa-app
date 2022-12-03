//
// Created by Himphen on 2022-11-25.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation

import Foundation
import SwiftUI
import shared

struct ItemSettingsRowView: View {
    
    @State var title: String
    @State var desc: String?
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(title)
                .frame(maxWidth: .infinity, alignment: .leading)
            if let desc {
                Text(desc)
                .font(.system(size: 14))
                .foregroundColor(Color.gray)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
    }
}
