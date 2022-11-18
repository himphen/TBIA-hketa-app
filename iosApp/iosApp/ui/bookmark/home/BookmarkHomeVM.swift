//
// Created by Himphen on 2022-11-08.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Combine
import SwiftUI
import shared
import Rswift

@MainActor class BookmarkHomeVM: ObservableObject {
    private var viewModel: BookmarkHomeViewModel? = nil
    
    @Published var hasData: Bool? = nil
    @Published var savedEtaCardList: [CardEtaCardItem]?
    @Published var lastUpdatedAgo: Int? = nil
    private var lastUpdatedTime: Int64? = nil
    @Published var etaError: Bool = false
    
    init() {
        viewModel = BookmarkHomeViewModel(
            savedEtaCardListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "savedEtaCardListUpdated"
                )
                
                DispatchQueue.main.async { [self] in
                    etaError = false
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
            },
            getEtaListFromDbFailed: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "getEtaListFromDbFailed"
                )
                
                etaError = true
                lastUpdatedTime = nil
            },
            updateEtaListFailed: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "updateEtaListFailed"
                )
                
                etaError = true
                lastUpdatedTime = nil
            }
        )
    }
    
    func getEtaListFromDb() async {
        do {
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
            CommonLoggerUtilsKt.logD(
                message: "updateLastUpdatedTime: \(lastUpdatedTime), \(DateUtilsKt.getCurrentTime().epochSeconds)"
            )
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
