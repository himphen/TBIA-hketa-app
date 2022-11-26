package hibernate.v2.utils

expect fun isDebugBuild(): Boolean

expect abstract class KMMContext

expect class KMMLocale

expect class KtorUnknownHostException

enum class TransportationLanguage {
    TC,
    SC,
    EN
}

data class LatLng(val latitude: Double, val longitude: Double)