package hibernate.v2.utils

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

actual fun StringResource.localized(context: KMMContext, vararg args: Any): String {
    if (args.isNotEmpty()) {
        return format(args).localized(context)
    }
    return desc().localized()
}

actual fun ResourceFormattedStringDesc.localized(context: KMMContext): String {
    return localized()
}

fun StringResource.localized(args: List<Any>? = null): String {
    if (args == null) {
        return localized(IOSContext())
    }

    return localized(IOSContext(), args)
}