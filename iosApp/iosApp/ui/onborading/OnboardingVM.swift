import SwiftUI
import shared

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
                    let first = viewModel?.fetchTransportDataFailedList.first
                    
                    loadingString = "載入九巴／龍運路線數據失敗，請重新再試。"
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
                
                loadingString = "正載入路線數據 （\(it.intValue)/\(fetchTransportDataRequiredCount)"
            }
        )
        
        do {
            try await viewModel?.checkDbTransportData()
        } catch {
        }
    }
}
