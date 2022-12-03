package hibernate.v2.utils

import hibernate.v2.tbia.BuildKonfig

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

fun reportEmailAddress(): String {
    return BuildKonfig.CONTACT_EMAIL
}