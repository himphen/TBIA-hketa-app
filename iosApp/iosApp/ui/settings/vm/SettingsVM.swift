//
// Created by Himphen on 2022-11-25.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import RswiftResources

@MainActor class SettingsVM: ObservableObject {
    private var viewModel: SettingsViewModel
    
    init() {
        viewModel = SettingsViewModel()
    }
    
    func resetTransportData() async {
        do {
            try await viewModel.resetTransportData()
        } catch {
        }
    }
    
    func updateLang(code: IOSLanguage.Code) {
        viewModel.updateLang(code: code)
    }
}
