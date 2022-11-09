package hibernate.v2.utils

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
        // TODO
        return false
    }

    actual fun isLangTC(context: KMMContext): Boolean {
        // TODO
        return true
    }
}