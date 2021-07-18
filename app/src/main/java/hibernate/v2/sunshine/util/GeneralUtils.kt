package hibernate.v2.sunshine.util

import android.content.Context
import android.content.pm.PackageManager

object GeneralUtils {
    fun getAppVersionName(context: Context?): String {
        val packageName = context?.packageName
        if (packageName.isNullOrEmpty()) return ""
        return try {
            context.packageManager.getPackageInfo(packageName, 0)?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException ) {
            ""
        }
    }
}