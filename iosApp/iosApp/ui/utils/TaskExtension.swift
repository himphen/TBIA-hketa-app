//
// Created by Himphen on 2022-11-14.
// Copyright (c) 2022 orgName. All rights reserved.
//

import Foundation

func tickerTask(period: UInt64, initialDelay: UInt64 = 0, action: @escaping () async -> Void) -> Task<Void, Error> {
    Task {
        do {
            try await Task.sleep(nanoseconds: initialDelay)
            while (!Task.isCancelled) {
                await action()
                try await Task.sleep(nanoseconds: period)
            }
        } catch {
        
        }
    }
}