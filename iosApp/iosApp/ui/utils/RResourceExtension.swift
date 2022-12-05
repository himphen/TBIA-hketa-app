//
// Created by Himphen on 2022-11-11.
// Copyright (c) 2022. All rights reserved.
//

import SwiftUI
import RswiftResources

// MARK: - ImageResource

extension ImageResource {
    var image: Image {
        Image(name)
    }
}

// MARK: - ColorResource

extension ColorResource {
    var color: Color {
        Color(name)
    }
}
