package hibernate.v2.utils

import io.github.aakira.napier.DebugAntilog

fun initLogger() {
    CommonLogger.base(DebugAntilog())
}