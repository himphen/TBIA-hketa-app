package hibernate.v2.tbia.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import hibernate.v2.MR
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.utils.TransportationLanguage
import hibernate.v2.utils.localized
import hibernate.v2.utils.reportEmailAddress
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Collections
import java.util.Locale

object GeneralUtils : KoinComponent {

    const val ETA_REFRESH_TIME = 10 * 1000L
    const val ETA_LAST_UPDATED_REFRESH_TIME = 1 * 1000L

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
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.resources.configuration.locales.get(0)
        else
            context.resources.configuration.locale

        return locale
    }

    fun getTransportationLanguage(context: Context): TransportationLanguage {
        return if (isLangEnglish(context)) {
            TransportationLanguage.EN
        } else {
            TransportationLanguage.TC
        }
    }

    fun isLangEnglish(context: Context): Boolean {
        return getLanguage(context)?.language == Locale.ENGLISH.language
    }

    fun isLangTC(context: Context): Boolean {
        return getLanguage(context)?.language == Locale.TRADITIONAL_CHINESE.language
    }

    fun report(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        var text = "Android Version: " + Build.VERSION.RELEASE + "\n"
        text += "SDK Level: " + Build.VERSION.SDK_INT + "\n"
        text += "Version: " + getAppVersionName(context) + "\n"
        text += "Brand: " + Build.BRAND + "\n"
        text += "Model: " + Build.MODEL + "\n\n\n"
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(reportEmailAddress()))
        intent.putExtra(Intent.EXTRA_SUBJECT, MR.strings.report_title.localized(context))
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(intent)
    }
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
