package hibernate.v2.utils

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.desc

actual fun StringResource.localized(context: KMMContext): String {
    return desc().localized()
}

actual fun ResourceFormattedStringDesc.localized(context: KMMContext): String {
    return localized()
}