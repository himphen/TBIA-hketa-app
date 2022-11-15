//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import Rswift

@MainActor
class BookmarkHomeVM: ObservableObject {
    private var viewModel: BookmarkHomeViewModel? = nil
    
    @Published var hasData: Bool? = nil
    @Published var savedEtaCardList: [CardEtaCardItem]?
    @Published var lastUpdatedAgo: Int? = nil
    private var lastUpdatedTime: Int? = nil
    @Published var etaError: Bool = false
    
    init() {
        viewModel = BookmarkHomeViewModel(
            savedEtaCardListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "savedEtaCardListUpdated"
                )
                
                etaError = false
                lastUpdatedTime = Int(DateUtilsKt.getCurrentTime().epochSeconds)
                
                let data = viewModel?.savedEtaCardList as! [Card.EtaCard]
                
                if var mSavedEtaCardList = savedEtaCardList {
                    CommonLoggerUtilsKt.logD(
                        message: "savedEtaCardList != nil"
                    )
                    mSavedEtaCardList.removeAll()
                    mSavedEtaCardList.append(contentsOf: data.map { element in
                        CardEtaCardItem(item: element)
                    })
                    
                    hasData = mSavedEtaCardList.count != 0
                    savedEtaCardList = mSavedEtaCardList
                } else {
                    CommonLoggerUtilsKt.logD(
                        message: "savedEtaCardList == nil"
                    )
                    hasData = data.count != 0
                    savedEtaCardList = data.map { element in
                        CardEtaCardItem(item: element)
                    }
                    
                    Task {
                        do {
                            try await viewModel?.updateEtaList()
                        } catch {
                        }
                    }
                }
                
            }
        )
    }
    
    func getEtaListFromDb() async {
        do {
            try await viewModel?.getEtaListFromDb()
        } catch {
        }
    }
    
    func updateEtaList() -> Task<Void, Error> {
        tickerTask(period: 60_000_000_000) { [self] in
            CommonLoggerUtilsKt.logD(
                message: "updateEtaList tickerTask"
            )
            do {
                try await viewModel?.updateEtaList()
            } catch {
                CommonLoggerUtilsKt.logD(
                    message: "etaUpdateError"
                )
                
                etaError = true
                lastUpdatedAgo = nil
            }
        }
    }
    
    func updateLastUpdatedTime() -> Task<Void, Error> {
        tickerTask(period: 1_000_000_000) { [self] in
            CommonLoggerUtilsKt.logD(
                message: "updateLastUpdatedTime tickerTask"
            )
            if let lastUpdatedTime = lastUpdatedTime {
                lastUpdatedAgo = Int(DateUtilsKt.getCurrentTime().epochSeconds) - lastUpdatedTime
            } else {
                lastUpdatedAgo = nil
            }
        }
    }
}

struct CardEtaCardItem: Hashable {
    let identifier = UUID()
    var item: Card.EtaCard
}