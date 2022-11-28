package hibernate.v2.utils

import dev.icerock.moko.graphics.colorInt
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.icerock.moko.resources.getColor

actual fun StringResource.localized(context: KMMContext, vararg args: Any): String {
    if (args.isNotEmpty()) {
        return format(*args).localized(context)
    }
    return desc().toString(context)
}

actual fun ResourceFormattedStringDesc.localized(context: KMMContext): String {
    return toString(context)
}

fun ColorResource.colorInt(context: KMMContext): Int {
    return getColor(context).colorInt()
}