import SwiftUI
import shared

struct OnboardingView: View {
    @StateObject var viewModel: OnboardingVM = OnboardingVM()
    
    @State var resetData: Bool = false
    
    var body: some View {
        if (!viewModel.isCompleted) {
            VStack {
                if viewModel.isFetchTransportDataRequired {
                    LottieView(name: "lottie_spaghetti_loader")
                    .frame(width: 200, height: 200)
                    Text("\(viewModel.loadingString)")
                } else {
                    R.image.app_icon.image
                    .resizable()
                    .scaledToFit()
                    .frame(
                        width: 200,
                        height: 200
                    )
                    .onAppear {
                        Task {
                            await viewModel.activate()
                        }
                    }
                }
            }
        } else {
            MainView(resetData: $resetData)
            .onChange(of: resetData) { newValue in
                if (newValue) {
                    resetData = false
                    viewModel.isCompleted = false
                }
            }
        }
    }
}
