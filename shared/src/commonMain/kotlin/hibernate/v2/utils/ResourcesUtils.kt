package hibernate.v2.utils

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc

expect fun StringResource.localized(context: KMMContext, vararg args: Any): String

expect fun ResourceFormattedStringDesc.localized(context: KMMContext): String