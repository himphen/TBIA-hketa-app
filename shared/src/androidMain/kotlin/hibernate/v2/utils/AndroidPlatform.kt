package hibernate.v2.utils

import hibernate.v2.api.BuildConfig

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun isDebugBuild(): Boolean {
    return BuildConfig.DEBUG
}