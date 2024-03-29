import SwiftUI
import shared
import FirebaseCore
import GoogleMaps

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        IOSCommonLoggerKt.doInitLogger()
        KoinModuleKt.doInitKoin()
        LanguageUtils().doInitLanguage()
    }
    
    var body: some Scene {
        WindowGroup {
            OnboardingView().environment(\.colorScheme, .light)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
  func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    FirebaseApp.configure()
    GMSServices.provideAPIKey(IOSPlatformKt.gmsApiKey())
    return true
  }
}
