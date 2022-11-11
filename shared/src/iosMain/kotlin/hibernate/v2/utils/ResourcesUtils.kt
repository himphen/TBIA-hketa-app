package hibernate.v2.utils

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

actual fun StringResource.localized(context: KMMContext): String {
    return desc().localized()
}

actual fun ResourceFormattedStringDesc.localized(context: KMMContext): String {
    return localized()
}

actual fun StringResource.formatString(context: KMMContext, args: List<Any>): String {
    return format(args).localized(context)
}

fun StringResource.formatString(args: List<Any>): String {
    return formatString(IOSContext(), args)
}