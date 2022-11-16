//
// Created by Himphen on 2022-11-16.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

// MARK: - ColorResource

extension ColorResource.Single {
    func toColor() -> Color {
        Color(
            red: (Double(color.red) / 255),
            green: (Double(color.green) / 255),
            blue: (Double(color.blue) / 255)
        )
    }
}

// MARK: - Color

extension GraphicsColor {
    func toColor() -> Color {
        Color(
            red: (Double(red) / 255),
            green: (Double(green) / 255),
            blue: (Double(blue) / 255)
        )
    }
}