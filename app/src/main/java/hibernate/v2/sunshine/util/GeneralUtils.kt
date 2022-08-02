package hibernate.v2.sunshine.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import hibernate.v2.sunshine.core.SharedPreferencesManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Collections
import java.util.Locale

object GeneralUtils : KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    fun getAppVersionName(context: Context?): String {
        val packageName = context?.packageName
        if (packageName.isNullOrEmpty()) return ""
        return try {
            context.packageManager.getPackageInfo(packageName, 0)?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    fun updateLanguage(context: Context): Context {
        val language = sharedPreferencesManager.language
        if (language.isNotEmpty()) {
            val config = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(Locale(language))
            } else {
                @Suppress("DEPRECATION")
                config.locale = Locale(language)
            }

            return context.createConfigurationContext(config)
        } else {
            val config = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(Locale.getDefault())
            } else {
                @Suppress("DEPRECATION")
                config.locale = Locale.getDefault()
            }

            return context.createConfigurationContext(config)
        }
    }

    fun getLanguage(context: Context): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.resources.configuration.locales.get(0)
        else
            context.resources.configuration.locale
    }

    fun isLangEnglish(context: Context): Boolean {
        return getLanguage(context) == Locale.ENGLISH
    }

    fun isLangTC(context: Context): Boolean {
        return getLanguage(context) == Locale.TRADITIONAL_CHINESE
    }

    const val ETA_REFRESH_TIME = 60 * 1000L
    const val ETA_LAST_UPDATED_REFRESH_TIME = 1 * 1000L
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