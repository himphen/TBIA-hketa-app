package hibernate.v2.utils

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun isDebugBuild(): Boolean

expect abstract class KMMContext

expect class KMMLocale

enum class TransportationLanguage {
    TC,
    SC,
    EN
}

data class LatLng(val latitude: Double, val longitude: Double)