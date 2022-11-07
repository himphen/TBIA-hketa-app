package hibernate.v2.utils

import platform.UIKit.UIDevice
import platform.darwin.NSObject

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun isDebugBuild(): Boolean {
    return true
}

actual typealias KMMContext = NSObject