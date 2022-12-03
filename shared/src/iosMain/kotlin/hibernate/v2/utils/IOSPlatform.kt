package hibernate.v2.utils

import platform.UIKit.UIDevice
import platform.darwin.NSObject

actual fun isDebugBuild(): Boolean {
    return true
}

abstract class AbstractIOSContext

class IOSContext : AbstractIOSContext()

actual typealias KMMContext = AbstractIOSContext

actual typealias KMMLocale = NSObject

actual typealias KtorUnknownHostException = NSObject

fun reportEmailContent(): String {
    var text =
        "OS Version: " + UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion() + "\n"
    text += "Version: " + platform.Foundation.NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String + "\n"
    text += "Model: " + UIDevice.currentDevice.model + "\n\n\n"

    return text
}