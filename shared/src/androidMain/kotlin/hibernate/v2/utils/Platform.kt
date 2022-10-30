package hibernate.v2.utils

import com.himphen.logger.Logger
import hibernate.v2.api.BuildConfig

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun isDebugBuild(): Boolean {
    return BuildConfig.DEBUG
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
        Logger.log(priority, tag, message, throwable)
    }

    actual fun i(message: String) {
        Logger.i(message)
    }

    actual fun d(`object`: Any?) {
        Logger.d(`object`)
    }

    actual fun w(message: String, vararg args: Any?) {
        Logger.w(message, *args)
    }

    actual fun e(message: String, vararg args: Any?) {
        Logger.e(null, message, *args)
    }

    actual fun e(throwable: Throwable?, message: String, vararg args: Any?) {
        Logger.e(throwable, message, *args)
    }
}