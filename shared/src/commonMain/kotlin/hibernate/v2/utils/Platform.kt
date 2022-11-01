package hibernate.v2.utils

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun isDebugBuild(): Boolean

enum class TransportationLanguage {
    TC,
    SC,
    EN
}