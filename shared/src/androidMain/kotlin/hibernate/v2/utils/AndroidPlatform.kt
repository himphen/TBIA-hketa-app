package hibernate.v2.utils

import android.content.Context
import hibernate.v2.BuildConfig
import java.net.UnknownHostException
import java.util.Locale

actual fun isDebugBuild(): Boolean {
    return BuildConfig.DEBUG
}

actual typealias KMMContext = Context

actual typealias KMMLocale = Locale

actual typealias KtorUnknownHostException = UnknownHostException

fun LatLng.toGoogleMapLatLng(): com.google.android.gms.maps.model.LatLng {
    return com.google.android.gms.maps.model.LatLng(latitude, longitude)
}

fun com.google.android.gms.maps.model.LatLng.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}