package hibernate.v2.utils

import io.github.aakira.napier.DebugAntilog

fun initCommonLogger() {
    CommonLogger.base(DebugAntilog())
}