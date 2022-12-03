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
    @Binding var resetData: Bool
    
    var body: some View {
        List {
            Section(header: Text(MR.strings().settings_category_settings_title.desc().localized())) {
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
                ItemSettingsRowView(
                    title: MR.strings().title_settings_report.desc().localized()
                )
                .contentShape(Rectangle())
                .onTapGesture {
                    openMail()
                }
                
                ItemSettingsRowView(title: MR.strings().title_settings_reset.desc().localized())
                .contentShape(Rectangle())
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

