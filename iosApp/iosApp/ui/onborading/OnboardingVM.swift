import SwiftUI
import shared
import Rswift

@MainActor class OnboardingVM: ObservableObject {
    private var viewModel: OnboardingViewModel? = nil
    
    @Published var loadingString: String = "正載入路線數據"
    @Published var isFetchTransportDataRequired = false
    @Published var isFetchTransportDataCompleted = false
    @Published var isCompleted = false
    
    func activate() async {
        viewModel = OnboardingViewModel(
            fetchTransportDataRequired: { [self] data in
                CommonLoggerUtilsKt.logD(
                    message: "fetchTransportDataRequired"
                )
                
                if (data.intValue < 0) {
                    return
                }
                
                if (data.intValue > 0) {
                    DispatchQueue.main.async { [self] in
                        isFetchTransportDataRequired = true
                    }
                } else {
                    CommonLoggerUtilsKt.logD(message: "Go next screen!!!")
                    
                    DispatchQueue.main.async { [self] in
                        isCompleted = true
                    }
                }
            },
            fetchTransportDataCannotInit: { [self] in
                CommonLoggerUtilsKt.logD(message:
                "fetchTransportDataCannotInit"
                )
            },
            fetchTransportDataCompleted: { [self] in
                CommonLoggerUtilsKt.logD(message:
                "fetchTransportDataCompleted"
                )
                
                let list = viewModel!.fetchTransportDataFailedList as NSArray as! [FailedCheckType]
                
                if (!list.isEmpty) {
                    let first: FailedCheckType = list.first!
                    switch (first) {
                    case FailedCheckType.kmb:
                        DispatchQueue.main.async { [self] in
                            loadingString = R.string.localizable.test_onboarding_loading_failed_kmb()
                        }
                    
                    default:
                        DispatchQueue.main.async { [self] in
                            loadingString = R.string.localizable.test_onboarding_loading_failed_other()
                        }
                    }
                } else {
                    CommonLoggerUtilsKt.logD(message: "Go next screen!")
                    
                    DispatchQueue.main.async { [self] in
                        isCompleted = true
                    }
                }
            },
            fetchTransportDataCompletedCount: { [self] data in
                CommonLoggerUtilsKt.logD(message:
                "fetchTransportDataCompletedCount"
                )
                let fetchTransportDataRequiredCount = viewModel?.fetchTransportDataRequiredCount ?? 0
    
                DispatchQueue.main.async { [self] in
                    loadingString = "正載入路線數據 （\(data.intValue)/\(fetchTransportDataRequiredCount))"
                }
            }
        )
        
        do {
            try await viewModel?.checkDbTransportData()
        } catch {
        }
    }
}
