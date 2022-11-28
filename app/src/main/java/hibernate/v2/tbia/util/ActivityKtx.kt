package hibernate.v2.tbia.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import kotlin.math.roundToInt

/**
 * Returns true when [Context] is unavailable or is about to become unavailable
 */
fun Context?.isDoomed(): Boolean = when (this) {
    null -> true
    is Application -> false
    is Activity -> (this.isDestroyed or this.isFinishing)
    else -> false
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
}

fun dpToPx(dp: Float): Int {
    return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
}

fun pxToDp(px: Int): Int {
    return (px / (Resources.getSystem().displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

inline fun <reified T : Enum<T>> Bundle.getEnum(key: String, default: T) =
    getInt(key).let { if (it >= 0) enumValues<T>()[it] else default }

fun <T : Enum<T>> Bundle.putEnum(key: String, value: T?) =
    putInt(key, value?.ordinal ?: -1)
