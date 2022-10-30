package hibernate.v2.utils

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun isDebugBuild(): Boolean

expect object CommonLogger {
    fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?)
    fun w(message: String, vararg args: Any?)
    fun d(`object`: Any?)
    fun i(message: String)
    fun e(message: String, vararg args: Any?)
    fun e(throwable: Throwable?, message: String, vararg args: Any?)
    val VERBOSE: Int
    val DEBUG_V: Int
    val INFO: Int
    val WARN: Int
    val ERROR: Int
    val ASSERT: Int
}

enum class TransportationLanguage {
    TC,
    SC,
    EN
}