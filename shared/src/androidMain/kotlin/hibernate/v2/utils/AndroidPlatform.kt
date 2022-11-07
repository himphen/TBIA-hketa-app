package hibernate.v2.utils

import android.app.Application
import hibernate.v2.api.BuildConfig

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun isDebugBuild(): Boolean {
    return BuildConfig.DEBUG
}

actual typealias KMMContext = Application

fun LatLng.toGoogleMapLatLng(): com.google.android.gms.maps.model.LatLng {
    return com.google.android.gms.maps.model.LatLng(latitude, longitude)
}

fun com.google.android.gms.maps.model.LatLng.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}