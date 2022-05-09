package hibernate.v2.sunshine.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Collections
import java.util.Locale

object GeneralUtils {
    fun getAppVersionName(context: Context?): String {
        val packageName = context?.packageName
        if (packageName.isNullOrEmpty()) return ""
        return try {
            context.packageManager.getPackageInfo(packageName, 0)?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    const val REFRESH_TIME = 60 * 1000L
}

fun <T> MutableList<T>.moveAt(oldIndex: Int, newIndex: Int) {
    val item = this[oldIndex]
    removeAt(oldIndex)
    if (oldIndex > newIndex)
        add(newIndex, item)
    else
        add(newIndex - 1, item)
}

/**
 * Swaps the position of two items at two respective indices
 */
fun <T> MutableList<T>.swap(indexOne: Int, indexTwo: Int) {
    Collections.swap(this, indexOne, indexTwo)
}

@SuppressLint("HardwareIds")
fun getAdMobDeviceID(context: Context): String {
    val androidId =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    return androidId.md5().uppercase(Locale.getDefault())
}

fun String.md5(): String {
    return try {
        val md = MessageDigest.getInstance("MD5")
        BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    } catch (e: Exception) {
        ""
    }
}