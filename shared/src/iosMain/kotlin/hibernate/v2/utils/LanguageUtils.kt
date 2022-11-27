package hibernate.v2.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual object LanguageUtils {

    actual fun getLanguage(context: KMMContext): KMMLocale? {
        return KMMLocale()
    }

    actual fun getTransportationLanguage(context: KMMContext): TransportationLanguage {
        return if (isLangEnglish(context)) {
            TransportationLanguage.EN
        } else {
            TransportationLanguage.TC
        }
    }

    actual fun isLangEnglish(context: KMMContext): Boolean {
        return NSLocale.currentLocale.languageCode.lowercase() == "en"
    }

    actual fun isLangTC(context: KMMContext): Boolean {
        return NSLocale.currentLocale.languageCode.lowercase() == "zh_TW"
    }
}