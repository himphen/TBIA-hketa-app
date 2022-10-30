package hibernate.v2.utils

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun isDebugBuild(): Boolean {
    return true
}

actual object CommonLogger {
    actual val VERBOSE = 2
    actual val DEBUG_V = 3
    actual val INFO = 4
    actual val WARN = 5
    actual val ERROR = 6
    actual val ASSERT = 7

    actual fun log(
        priority: Int,
        tag: String?,
        message: String?,
        throwable: Throwable?
    ) {
    }

    actual fun w(message: String, vararg args: Any?) {
    }

    actual fun d(`object`: Any?) {
    }

    actual fun i(message: String) {
    }

    actual fun e(message: String, vararg args: Any?) {
    }

    actual fun e(throwable: Throwable?, message: String, vararg args: Any?) {
    }
}