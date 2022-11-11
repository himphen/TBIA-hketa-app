//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct RouteListView: View {
    @ObservedObject var viewModel: RouteListVM = RouteListVM()
    
    var body: some View {
        VStack {
            SearchBar(text: .constant(""))
            
            NavigationView {
                VStack {
                    List(viewModel.filteredTransportRouteList, id: \.self) { item in
                        ItemRouteListView(route: item, etaType: EtaType.kmb)
                        .listRowInsets(EdgeInsets())
                    }
                    .listStyle(PlainListStyle())
                    .navigationBarTitle("九巴")
                    .task {
                        await viewModel.activate()
                    }
                }
            }
        }
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