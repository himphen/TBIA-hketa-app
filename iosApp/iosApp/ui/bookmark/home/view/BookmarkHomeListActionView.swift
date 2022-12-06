//
//  BookmarkHomeListActionView.swift
//  iosApp
//
//  Created by Himphen on 2022-11-22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import SwiftUI
import shared
import RswiftResources

struct BookmarkHomeListActionView: View {
    @State private var action: Int? = 0
    @Binding var selectedAppearance: Int
    
    var body: some View {
        HStack(spacing: 0) {
            NavigationLink(
                destination: RouteListView(),
                tag: 1, selection: $action
            ) {
            }
            
            NavigationLink(
                destination: BookmarkEditView(),
                tag: 2, selection: $action
            ) {
            }
            
            Button(action: {
                self.action = 1
                self.selectedAppearance = 1
            }) {
                HStack {
                    Image("ic_search_24")
                    .foregroundColor(Color.blue)
                    Text(MR.strings().eta_button_add.localized())
                    .font(.system(size: 18))
                    .foregroundColor(Color.blue)
                }
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 32)
                    .stroke(Color.gray, lineWidth: 2)
                )
                .cornerRadius(32)
            }
            
            Spacer()
            .frame(width: 8)
            
            Button(action: {
                self.action = 2
                self.selectedAppearance = 1
            }) {
                HStack {
                    Image("ic_edit_24")
                    .foregroundColor(Color.blue)
                    Text(MR.strings().eta_button_edit.localized())
                    .font(.system(size: 18))
                    .foregroundColor(Color.blue)
                }
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 32)
                    .stroke(Color.gray, lineWidth: 2)
                )
                .cornerRadius(32)
            }
        }
        .padding(12)
    }
}
