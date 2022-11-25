//
// Created by Himphen on 2022-11-21.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import Rswift

struct SettingsView: View {
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) var scenePhase
    
    @StateObject var viewModel: SettingsVM = SettingsVM()
    @State var showingResetConfirmation = false
    @Binding var resetData: Bool
    
    var body: some View {
        List {
            Section(header: Text(MR.strings().settings_category_settings_title.desc().localized())) {
//                Text(MR.strings().title_settings_language.desc().localized())
                ItemSettingsRowView(
                    title: MR.strings().title_settings_version.desc().localized(),
                    desc: Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
                )
            }
            
            Section(header: Text(MR.strings().settings_category_notices_title.desc().localized())) {
                ItemSettingsRowView(
                    title: MR.strings().settings_acknowledgement_title1.desc().localized(),
                    desc: MR.strings().settings_acknowledgement_summary1.desc().localized()
                )
                ItemSettingsRowView(
                    title: MR.strings().settings_acknowledgement_title2.desc().localized(),
                    desc: MR.strings().settings_acknowledgement_summary2.desc().localized()
                )
                ItemSettingsRowView(
                    title: MR.strings().settings_acknowledgement_title3.desc().localized(),
                    desc: MR.strings().settings_acknowledgement_summary3.desc().localized()
                )
            }
            
            Section(header: Text(MR.strings().settings_category_debug_title.desc().localized())) {
                Text(MR.strings().title_settings_reset.desc().localized())
                .frame(maxWidth: .infinity)
                .onTapGesture {
                    showingResetConfirmation = true
                }
                .confirmationDialog(MR.strings().title_settings_reset.desc().localized(), isPresented: $showingResetConfirmation) {
                    Button(MR.strings().dialog_confirm_btn.desc().localized(), role: .destructive) {
                        Task {
                            await viewModel.resetTransportData()
                            
                            resetData = true
                        }
                    }
                    Button(MR.strings().dialog_cancel_btn.desc().localized(), role: .cancel) {
                    }
                } message: {
                    Text(MR.strings().dialog_settings_reset_message.desc().localized())
                }
            }
            
            Section(header: Text("")) {
                EmptyView()
            }
            .padding(.bottom, 100)
            
        }
        .listStyle(.grouped)
        .preferredColorScheme(.light)
    }
}

