import SwiftUI
import shared

struct OnboardingView: View {
    @ObservedObject var viewModel: OnboardingVM = OnboardingVM()
    
    var body: some View {
        VStack {
            Text("Checksum: \(viewModel.checksumString)")
                    .onAppear(perform: {
                        viewModel.activate()
            })
        }
    }
}
