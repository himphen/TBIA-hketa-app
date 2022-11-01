package hibernate.v2.utils

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

typealias CommonLogger = Napier

fun initCommonLogger() {
    CommonLogger.base(DebugAntilog())
}

fun logLifecycle(message: String) {
    CommonLogger.d(tag = "lifecycle") { message }
}