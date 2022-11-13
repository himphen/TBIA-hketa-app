import SwiftUI
import shared
import FirebaseCore

@main
struct iOSApp: App {
    
    init() {
        IOSCommonLoggerKt.doInitLogger()
        FirebaseApp.configure()
        KoinModuleKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            RouteListView().environment(\.colorScheme, .light)
//            BookmarkHomeView()
//            OnboardingView()
        }
    }
}
