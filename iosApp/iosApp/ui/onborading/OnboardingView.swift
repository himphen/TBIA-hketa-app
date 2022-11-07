import SwiftUI
import shared

struct OnboardingView: View {
    @ObservedObject var viewModel: OnboardingVM = OnboardingVM()
    
    var body: some View {
        VStack {
            if viewModel.isFetchTransportDataRequired {
                LottieView(name: "lottie_spaghetti_loader")
                        .frame(width: 200, height: 200)
                Text("\(viewModel.loadingString)")
            } else {
                Text("Welcome").task({
                    await viewModel.activate()
                })
            }
        }
    }
}
