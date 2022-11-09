package hibernate.v2.utils

import java.util.Locale

actual object LanguageUtils {

    actual fun getLanguage(context: KMMContext): Locale? {
        return context.resources.configuration.locales.get(0)
    }

    actual fun getTransportationLanguage(context: KMMContext): TransportationLanguage {
        return if (isLangEnglish(context)) {
            TransportationLanguage.EN
        } else {
            TransportationLanguage.TC
        }
    }

    actual fun isLangEnglish(context: KMMContext): Boolean {
        return getLanguage(context)?.language == Locale.ENGLISH.language
    }

    actual fun isLangTC(context: KMMContext): Boolean {
        return getLanguage(context)?.language == Locale.TRADITIONAL_CHINESE.language
    }
}