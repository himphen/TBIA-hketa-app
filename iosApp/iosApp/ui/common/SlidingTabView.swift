import SwiftUI
import shared

struct RouteListTab: BaseTab {
    var identifier = UUID()
    var icon: Image?
    var title: String
    var color: Color
    var etaType: EtaType
}

protocol BaseTab: Equatable {
    var identifier: UUID { get }
    var icon: Image? { get }
    var title: String { get }
    var color: Color { get }
}

struct Tabs: View {
    var fixed = true
    var tabs: [any BaseTab]
    var geoWidth: CGFloat
    @Binding var selectedTab: Int
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            ScrollViewReader { proxy in
                VStack(spacing: 0) {
                    HStack(spacing: 0) {
                        ForEach(0..<tabs.count, id: \.self) { row in
                            let tab = tabs[row]
                            Button(action: {
                                withAnimation {
                                    selectedTab = row
                                }
                            }, label: {
                                VStack(spacing: 0) {
                                    HStack(spacing: 0) {
                                        // Image
                                        AnyView(tab.icon)
                                        .foregroundColor(tab.color)
                                        .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
                                        // Text
                                        Text(tab.title)
                                        .font(Font.system(size: 16, weight: .bold))
                                        .foregroundColor(Color.black)
                                        .padding(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 15))
                                    }
                                    .frame(width: fixed ? (geoWidth / CGFloat(tabs.count)) : .none, height: 52)
                                    // Bar Indicator
                                    Rectangle().fill(selectedTab == row ? tab.color : Color.clear)
                                    .frame(height: 3)
                                }
                                .fixedSize()
                            })
                            .accentColor(Color.white)
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .onChange(of: selectedTab) { target in
                        withAnimation {
                            proxy.scrollTo(target)
                        }
                    }
                }
            }
        }
        .frame(height: 55)
        .onAppear(perform: {
            UIScrollView.appearance().bounces = fixed ? false : true
        })
        .onDisappear(perform: {
            UIScrollView.appearance().bounces = true
        })
    }
}