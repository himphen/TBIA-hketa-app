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
import UniformTypeIdentifiers

struct BookmarkEditView: View {
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) var scenePhase
    
    @StateObject var viewModel: BookmarkEditVM = BookmarkEditVM()
    
    init() {
       UIScrollView.appearance().bounces = false
    }
    
    var body: some View {
        VStack(spacing: 0) {
            List{
                ForEach(Array(viewModel.savedEtaCardList.enumerated()), id: \.element.identifier) { index, item in
                    ItemBookmarkEditView(card: item.item)
                        .frame(
                            maxWidth: .infinity
                        )
                        .listRowInsets(EdgeInsets())
                        .listRowSeparator(.hidden)
                        .swipeActions(edge: .trailing) {
                            Button(action: {
                                Task {
                                    await viewModel.removeEta(item: item.item.entity)
                                    viewModel.savedEtaCardList.remove(at: index)
                                }
                            } ) {
                                Text(MR.strings().eta_button_remove.localized())
                            }
                            .tint(Color(UIColor.systemOrange))
                        }
                        .onDrag({
                            viewModel.draggedItem = item
                            return NSItemProvider(item: nil, typeIdentifier: item.identifier.uuidString)
                        })
                        .onDrop(
                            of: [UTType.text],
                            delegate: DragAndDropService<CardSettingsEtaItemCardItem>(
                                currentItem: item,
                                items: $viewModel.savedEtaCardList,
                                draggedItem: $viewModel.draggedItem
                            )
                        )
                }
                .onMove(perform: move)
            }
            .listStyle(.plain)
        }
        .onChange(of: viewModel.savedEtaCardList) { list in
            if (list.isEmpty) {
                presentationMode.wrappedValue.dismiss()
            }
        }
        .onAppear {
            Task {
                await viewModel.getSavedEtaCardList()
            }
        }
        .navigationBarTitle(
            MR.strings.shared.eta_button_edit.localized(),
            displayMode: .inline
        )
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button {
                    Task {
                        await viewModel.updateEtaOrderList()
                        dismiss()
                    }
                } label: {
                    HStack {
                        Image(systemName: "chevron.left")
                        Text(MR.strings().dialog_check_edit_eta_order_save_btn.localized())
                    }
                }
            }
        }
        .preferredColorScheme(.light)
    }
    
    func move(from source: IndexSet, to destination: Int) {        
        viewModel.savedEtaCardList.move(fromOffsets: source, toOffset: destination)
    }
    
    private func dismiss() {
        presentationMode.wrappedValue.dismiss()
    }
}

struct DragAndDropService<T: Equatable>: DropDelegate {
    let currentItem: T
    @Binding var items: [T]
    @Binding var draggedItem: T?
    
    func performDrop(info: DropInfo) -> Bool {
        return true
    }
    
    func dropEntered(info: DropInfo) {
        guard let draggedItem = draggedItem,
              draggedItem != currentItem,
              let from = items.firstIndex(of: draggedItem),
              let to = items.firstIndex(of: currentItem)
        else {
            return
        }
        withAnimation {
            items.move(fromOffsets: IndexSet(integer: from), toOffset: to > from ? to + 1 : to)
        }
    }
    
}
