package hibernate.v2.utils

import platform.darwin.NSObject

actual fun isDebugBuild(): Boolean {
    return true
}

abstract class AbstractIOSContext

class IOSContext : AbstractIOSContext()

actual typealias KMMContext = AbstractIOSContext

actual typealias KMMLocale = NSObject

actual typealias KtorUnknownHostException = NSObject