import SwiftUI
import shared

class OnboardingVM: ObservableObject {
    private var viewModel: OnboardingViewModel? = nil

    @Published var checksum: Checksum? = nil
    @Published var checksumString: String = "N/A"

    func activate(){
        viewModel = OnboardingViewModel { [weak self] dataState in
            self?.checksum = dataState
            self?.checksumString = "\(dataState)"
        }

        viewModel?.checkDbTransportData()
    }
}
