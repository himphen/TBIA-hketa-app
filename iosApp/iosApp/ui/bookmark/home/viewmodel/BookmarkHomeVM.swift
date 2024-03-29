//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Combine
import SwiftUI
import shared
import RswiftResources

@MainActor class BookmarkHomeVM: ObservableObject {
    private var viewModel: BookmarkHomeViewModel? = nil
    
    @Published var hasData: Bool? = nil
    @Published var savedEtaCardList: [CardEtaCardItem]?
    @Published var lastUpdatedAgo: Int? = nil
    private var lastUpdatedTime: Int64? = nil
    @Published var etaError: String? = nil
    
    private var waitFirstEta: Bool = false
    
    init() {
        viewModel = BookmarkHomeViewModel(
            savedEtaCardListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "savedEtaCardListUpdated"
                )
                
                DispatchQueue.main.async { [self] in
                    etaError = nil
                    lastUpdatedAgo = 0
                    lastUpdatedTime = DateUtilsKt.getCurrentTime().epochSeconds
                }
                
                let data = viewModel?.savedEtaCardList as! [Card.EtaCard]
                
                if var mSavedEtaCardList = savedEtaCardList {
                    CommonLoggerUtilsKt.logD(
                        message: "savedEtaCardList != nil"
                    )
                    mSavedEtaCardList.removeAll()
                    mSavedEtaCardList.append(contentsOf: data.map { element in
                        CardEtaCardItem(item: element)
                    })
                    
                    DispatchQueue.main.async { [self] in
                        hasData = mSavedEtaCardList.count != 0
                        savedEtaCardList = mSavedEtaCardList
                    }
                } else {
                    CommonLoggerUtilsKt.logD(
                        message: "savedEtaCardList == nil"
                    )
                    
                    DispatchQueue.main.async { [self] in
                        hasData = data.count != 0
                        savedEtaCardList = data.map { element in
                            CardEtaCardItem(item: element)
                        }
                    }
                }
                
                if (waitFirstEta) {
                    waitFirstEta = false
                    Task {
                        do {
                            try await viewModel?.updateEtaList()
                        } catch {
                        }
                    }
                }
            },
            getEtaListFromDbFailed: { [self] data in
                CommonLoggerUtilsKt.logD(
                    message: "getEtaListFromDbFailed"
                )
                
                DispatchQueue.main.async { [self] in
                    etaError = data
                    lastUpdatedTime = nil
                }
            },
            updateEtaListFailed: { [self] data in
                CommonLoggerUtilsKt.logD(
                    message: "updateEtaListFailed"
                )
                
                DispatchQueue.main.async { [self] in
                    etaError = data
                    lastUpdatedTime = nil
                }
            }
        )
    }
    
    func getEtaListFromDb() async {
        do {
            waitFirstEta = true
            try await viewModel?.getEtaListFromDb()
        } catch {
        }
    }
    
    func updateEtaList() -> Combine.Cancellable {
        DispatchQueue
        .global(qos: .utility)
        .schedule(after: DispatchQueue.SchedulerTimeType(.now()),
            interval: .seconds(60),
            tolerance: .seconds(60 / 5)) { [self] in
            Task {
                CommonLoggerUtilsKt.logD(
                    message: "updateEtaList tickerTask"
                )
                do {
                    try await viewModel?.updateEtaList()
                } catch {
                
                }
            }
        }
    }
    
    func updateLastUpdatedTime() -> Combine.Cancellable {
        DispatchQueue
        .global(qos: .utility)
        .schedule(after: DispatchQueue.SchedulerTimeType(.now()),
            interval: .seconds(1),
            tolerance: .seconds(1 / 5)) { [self] in
            if let lastUpdatedTime = lastUpdatedTime {
                DispatchQueue.main.async { [self] in
                    lastUpdatedAgo = Int((DateUtilsKt.getCurrentTime().epochSeconds - lastUpdatedTime))
                }
            } else {
                DispatchQueue.main.async { [self] in
                    lastUpdatedAgo = nil
                }
            }
        }
    }
}

struct CardEtaCardItem: Hashable {
    let identifier = UUID()
    var item: Card.EtaCard
}
