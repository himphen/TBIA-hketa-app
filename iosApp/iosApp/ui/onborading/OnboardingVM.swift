import SwiftUI
import shared
import Rswift

@MainActor
class OnboardingVM: ObservableObject {
    private var viewModel: OnboardingViewModel? = nil
    
    @Published var loadingString: String = "正載入路線數據"
    @Published var isFetchTransportDataRequired = false
    @Published var isFetchTransportDataCompleted = false
    
    func activate() async {
        viewModel = OnboardingViewModel(
            fetchTransportDataRequired: { [self] it in
                CommonLoggerUtilsKt.logD(
                    message: "fetchTransportDataRequired"
                )
                
                if (it.intValue < 0) {
                    return
                }
                
                if (it.intValue > 0) {
                    isFetchTransportDataRequired = true
                } else {
                    CommonLoggerUtilsKt.logD(message:
                    "Go next screen!!!"
                    )
                }
            },
            fetchTransportDataCannotInit: { [self] it in
                CommonLoggerUtilsKt.logD(message:
                "fetchTransportDataCannotInit"
                )
            },
            fetchTransportDataCompleted: { [self] it in
                CommonLoggerUtilsKt.logD(message:
                "fetchTransportDataCompleted"
                )
                
                if (viewModel?.fetchTransportDataFailedList.count != 0) {
                    let first: FailedCheckType = viewModel?.fetchTransportDataFailedList.firstObject as! FailedCheckType
                    switch (first) {
                    case FailedCheckType.kmb:
                        loadingString = R.string.localizable.test_onboarding_loading_failed_kmb()
                    
                    default:
                        loadingString = R.string.localizable.test_onboarding_loading_failed_other()
                    }
                } else {
                    
                    loadingString = "載入完成。"
                    CommonLoggerUtilsKt.logD(message:
                    "Go next screen!"
                    )
                }
            },
            fetchTransportDataCompletedCount: { [self] it in
                CommonLoggerUtilsKt.logD(message:
                "fetchTransportDataCompletedCount"
                )
                let fetchTransportDataRequiredCount = viewModel?.fetchTransportDataRequiredCount ?? 0
                
                loadingString = "正載入路線數據 （\(it.intValue)/\(fetchTransportDataRequiredCount))"
            }
        )
        
        do {
            try await viewModel?.checkDbTransportData()
        } catch {
        }
    }
}
