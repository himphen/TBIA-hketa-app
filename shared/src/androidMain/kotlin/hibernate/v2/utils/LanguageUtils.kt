package hibernate.v2.utils

import hibernate.v2.core.SharedPreferencesManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

actual object LanguageUtils : KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    actual fun getLanguage(context: KMMContext): Locale {
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
        return getLanguage(context).language == Locale.ENGLISH.language
    }

    actual fun isLangTC(context: KMMContext): Boolean {
        return getLanguage(context).language == Locale.TRADITIONAL_CHINESE.language
    }
}