package hibernate.v2.sunshine.util

import android.app.Activity
import android.app.Application
import android.content.Context

/**
 * Returns true when [Context] is unavailable or is about to become unavailable
 */
fun Context?.isDoomed(): Boolean = when (this) {
    null -> true
    is Application -> false
    is Activity -> (this.isDestroyed or this.isFinishing)
    else -> false
}