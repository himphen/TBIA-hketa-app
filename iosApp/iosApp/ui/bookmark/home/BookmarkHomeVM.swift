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
    
    
    func activate() async {
        viewModel = BookmarkHomeViewModel(
            savedEtaCardListUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "savedEtaCardListUpdated"
                )
            },
            etaUpdateError: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "etaUpdateError"
                )
            },
            etaRequested: { [self] data in
                CommonLoggerUtilsKt.logD(
                    message: "etaRequested"
                )
            },
            lastUpdatedTimeUpdated: { [self] in
                CommonLoggerUtilsKt.logD(
                    message: "lastUpdatedTimeUpdated"
                )
            }
        )
        
        do {
            try await viewModel?.getEtaListFromDb()
        } catch {
        }
    }
}