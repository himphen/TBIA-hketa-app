//
// Created by Himphen on 2022-11-21.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import Rswift
import FirebaseAnalytics
import FirebaseCrashlytics
import ThirdPartyMailer

struct SettingsView: View {
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) var scenePhase
    
    @StateObject var viewModel: SettingsVM = SettingsVM()
    @State var showingResetConfirmation = false
    @State var showingLangConfirmation = false
    @Binding var resetData: Bool
    @State private var showingLangChangeAlert = false
    
    var body: some View {
        List {
            Section(header: Text(MR.strings().settings_category_settings_title.localized())) {
                ItemSettingsRowView(title: MR.strings().title_settings_language.localized())
                .contentShape(Rectangle())
                .onTapGesture {
                    showingLangConfirmation = true
                }
                .confirmationDialog(MR.strings().title_settings_language.localized(), isPresented: $showingLangConfirmation) {
                    Button(MR.strings().settings_lang_option_default.localized()) { [self] in
                        viewModel.updateLang(code: IOSLanguage.Code.default_)
                        
                        showingLangChangeAlert = true
                    }
                    Button(MR.strings().settings_lang_option_en.localized()) { [self] in
                        viewModel.updateLang(code: IOSLanguage.Code.en)
                        
                        showingLangChangeAlert = true
                    }
                    Button(MR.strings().settings_lang_option_zh_tw.localized()) { [self] in
                        viewModel.updateLang(code: IOSLanguage.Code.zhTw)
                        
                        showingLangChangeAlert = true
                    }
                    Button(MR.strings().dialog_cancel_btn.localized(), role: .cancel) {
                    }
                }
                .alert(MR.strings().settings_lang_restart_toast_message.localized(), isPresented: $showingLangChangeAlert) {
                    Button(MR.strings().dialog_confirm_btn.localized(), role: .cancel) {
                        showingLangChangeAlert = false
                        resetData = true
                    }
                }
                ItemSettingsRowView(
                    title: MR.strings().title_settings_version.localized(),
                    desc: Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
                )
            }
            
            Section(header: Text(MR.strings().settings_category_notices_title.localized())) {
                ItemSettingsRowView(
                    title: MR.strings().settings_acknowledgement_title1.localized(),
                    desc: MR.strings().settings_acknowledgement_summary1.localized()
                )
                ItemSettingsRowView(
                    title: MR.strings().settings_acknowledgement_title2.localized(),
                    desc: MR.strings().settings_acknowledgement_summary2.localized()
                )
                ItemSettingsRowView(
                    title: MR.strings().settings_acknowledgement_title3.localized(),
                    desc: MR.strings().settings_acknowledgement_summary3.localized()
                )
            }
            
            Section(header: Text(MR.strings().settings_category_debug_title.localized())) {
                ItemSettingsRowView(
                    title: MR.strings().title_settings_report.localized()
                )
                .contentShape(Rectangle())
                .onTapGesture {
                    openMail()
                }
                
                ItemSettingsRowView(title: MR.strings().title_settings_reset.localized())
                .contentShape(Rectangle())
                .onTapGesture {
                    showingResetConfirmation = true
                }
                .confirmationDialog(MR.strings().title_settings_reset.localized(), isPresented: $showingResetConfirmation) {
                    Button(MR.strings().dialog_confirm_btn.localized(), role: .destructive) {
                        Task {
                            await viewModel.resetTransportData()
                            
                            resetData = true
                        }
                    }
                    Button(MR.strings().dialog_cancel_btn.localized(), role: .cancel) {
                    }
                } message: {
                    Text(MR.strings().dialog_settings_reset_message.localized())
                }
                
                #if DEBUG
                    ItemSettingsRowView(title: "Test Crash")
                    .frame(maxWidth: .infinity)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        CommonLoggerUtilsKt.logD(message: "Test Crash")
                        fatalError("Crash was triggered")
                    }
                #endif

                #if DEBUG
                    ItemSettingsRowView(title: "Test Analytics")
                    .frame(maxWidth: .infinity)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        CommonLoggerUtilsKt.logD(message: "Test Analytics")
                        Analytics.logEvent(AnalyticsEventSelectContent, parameters: [
                          AnalyticsParameterItemID: "test ID",
                          AnalyticsParameterItemName: "test title",
                          AnalyticsParameterContentType: "test cont",
                        ])
                    }
                #endif
            }
            
            Section(header: Text("")) {
                EmptyView()
            }
            .padding(.bottom, 100)
            
        }
        .listStyle(.grouped)
        .preferredColorScheme(.light)
    }
    
    func openMail() {
        let body = IOSPlatformKt.reportEmailContent()
        let to = PlatformKt.reportEmailAddress()
        let subject = MR.strings().report_title.localized()
        
        let client = ThirdPartyMailClient.clients
        
        if ThirdPartyMailer.isMailClientAvailable(client[1]) {
            ThirdPartyMailer.openCompose(client[1], recipient: to, subject: subject, body: body)
        } else if ThirdPartyMailer.isMailClientAvailable(client[5]) {
            ThirdPartyMailer.openCompose(client[5], recipient: to, subject: subject, body: body)
        } else {
            ThirdPartyMailer.openCompose(recipient: to, subject: subject, body: body)
        }
        
    }
}

