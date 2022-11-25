//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Combine
import SwiftUI
import shared
import Rswift

@MainActor class BookmarkEditVM: ObservableObject {
    private var viewModel: BookmarkEditViewModel? = nil
    
    @Published var savedEtaCardList: [CardSettingsEtaItemCardItem] = []
    @Published var hasInvalidCard = false
    
    @Published var draggedItem: CardSettingsEtaItemCardItem?
    
    var invalidCardList = [Card.SettingsEtaItemCard]()
    
    init() {
        viewModel = BookmarkEditViewModel(
            savedEtaCardListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "savedEtaCardListUpdated"
                )
                
                DispatchQueue.main.async { [self] in
                    let data = viewModel?.savedEtaCardList as! [Card.SettingsEtaCard]
                    
                    savedEtaCardList.removeAll()
                    
                    let cardList = data.compactMap { card1 -> CardSettingsEtaItemCardItem? in
                        let card = card1 as! Card.SettingsEtaItemCard
                        if (!card.isValid) {
                            invalidCardList.append(card)
                            return nil
                        }
                        return CardSettingsEtaItemCardItem(item: card)
                    }
                    
                    savedEtaCardList.append(contentsOf: cardList)
                    
                    if (!invalidCardList.isEmpty) {
                        Task {
                            for item in invalidCardList {
                                await removeEta(item: item.entity)
                            }
                        }
                        hasInvalidCard = true
                    }
                    
                }
            }
        )
    }
    
    func getSavedEtaCardList() async {
        do {
            try await viewModel?.getSavedEtaCardList()
        } catch {
        }
    }
    
    func removeEta(item: SavedEtaEntity) async {
        do {
            try await viewModel?.removeEta(item: item)
        } catch {
        }
    }
    
    func updateEtaOrderList() async {
        let entityList = savedEtaCardList.enumerated().map { index, item -> SavedEtaOrderEntity in
            SavedEtaOrderEntity(
                id: item.item.entity.id,
                position: Int32(index)
            )
        }
        
        do {
            try await viewModel?.updateEtaOrderList(entityList: entityList)
        } catch {
        }
    }
}


struct CardSettingsEtaItemCardItem: Hashable {
    let identifier = UUID()
    var item: Card.SettingsEtaItemCard
}
