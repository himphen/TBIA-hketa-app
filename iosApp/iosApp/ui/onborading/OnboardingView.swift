import SwiftUI
import shared

struct OnboardingView: View {
    @StateObject var viewModel: OnboardingVM = OnboardingVM()
    
    @State var resetData: Bool = false
    
    var body: some View {
        if (!viewModel.isCompleted) {
            VStack {
                if (viewModel.isFetchTransportDataRequired || viewModel.isFailed) {
                    LottieView(name: "lottie_spaghetti_loader")
                    .frame(width: 200, height: 200)
                    Text("\(viewModel.loadingString)")
                        .padding(EdgeInsets(top: 12, leading: 24, bottom: 12, trailing: 24))
                    
                    if (viewModel.isFailed) {
                        HStack(spacing: 0) {
                            Button(action: {
                                viewModel.isFetchTransportDataRequired = false
                                viewModel.isFailed = false
                            } ) {
                                Text(MR.strings().onboarding_retry_btn.localized())
                            }
                            .padding()
                            .foregroundColor(.white)
                            .background(MR.colors().primary.toColor())
                            .cornerRadius(40)
                        }
                    }
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
                            await viewModel.checkDbTransportData()
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
                    viewModel.isFetchTransportDataRequired = false
                }
            }
        }
    }
}
