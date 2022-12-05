import SwiftUI
import shared
import Rswift

@MainActor class OnboardingVM: ObservableObject {
    private var viewModel: OnboardingViewModel? = nil
    
    @Published var loadingString: String = ""
    @Published var isFetchTransportDataRequired = false
    @Published var isFetchTransportDataCompleted = false
    @Published var isCompleted = false
    
    init() {
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
                            loadingString = MR.strings().test_onboarding_loading_failed_kmb.localized()
                        }
                    case FailedCheckType.ctb:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_ctb.localized()
                        }
                    case FailedCheckType.gmb:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_gmb.localized()
                        }
                    case FailedCheckType.mtr:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_mtr.localized()
                        }
                    case FailedCheckType.lrt:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_lrt.localized()
                        }
                    case FailedCheckType.nlb:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_nlb.localized()
                        }
                    case FailedCheckType.checksum:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_checksum.localized()
                        }
                    default:
                        DispatchQueue.main.async { [self] in
                            loadingString = MR.strings().test_onboarding_loading_failed_other.localized()
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
                    loadingString = MR.strings().test_onboarding_loading.localized(args: [String(data.intValue), String(fetchTransportDataRequiredCount)])
                }
            }
        )
    }
    
    func activate() async {
        do {
            try await viewModel?.checkDbTransportData()
        } catch {
        }
    }
}
