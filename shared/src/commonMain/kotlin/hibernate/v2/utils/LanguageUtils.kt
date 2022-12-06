package hibernate.v2.utils

import org.koin.core.component.KoinComponent

expect object LanguageUtils : KoinComponent {

    fun getLanguage(context: KMMContext): KMMLocale

    fun getTransportationLanguage(context: KMMContext): TransportationLanguage

    fun isLangEnglish(context: KMMContext): Boolean

    fun isLangTC(context: KMMContext): Boolean
}