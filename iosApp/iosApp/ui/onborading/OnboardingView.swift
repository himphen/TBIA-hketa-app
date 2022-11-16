import SwiftUI
import shared

struct OnboardingView: View {
    @ObservedObject var viewModel: OnboardingVM = OnboardingVM()
    
    var body: some View {
        if (!viewModel.isCompleted) {
            VStack {
                if viewModel.isFetchTransportDataRequired {
                    LottieView(name: "lottie_spaghetti_loader")
                    .frame(width: 200, height: 200)
                    Text("\(viewModel.loadingString)")
                } else {
                    Text(MR.strings().test_onboarding_loading_failed_kmb.desc().localized()).task({
                        await viewModel.activate()
                    })
                }
            }
        } else {
            BookmarkHomeView()
        }
    }
}
