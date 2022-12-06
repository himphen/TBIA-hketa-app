package hibernate.v2.utils

import io.github.aakira.napier.Napier

typealias CommonLogger = Napier

fun logLifecycle(message: String) {
    CommonLogger.d(tag = "lifecycle") { message }
}

fun logLifecycle(e: Exception, message: String) {
    CommonLogger.e(throwable = e, tag = "lifecycle", message = message)
}

fun logD(message: String) {
    CommonLogger.d(message)
}