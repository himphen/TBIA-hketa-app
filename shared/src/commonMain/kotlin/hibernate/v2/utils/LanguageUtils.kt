package hibernate.v2.utils

expect object LanguageUtils {

    fun getLanguage(context: KMMContext): KMMLocale?

    fun getTransportationLanguage(context: KMMContext): TransportationLanguage

    fun isLangEnglish(context: KMMContext): Boolean

    fun isLangTC(context: KMMContext): Boolean
}