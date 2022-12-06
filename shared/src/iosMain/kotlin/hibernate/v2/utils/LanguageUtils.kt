package hibernate.v2.utils

import dev.icerock.moko.resources.desc.StringDesc
import hibernate.v2.core.SharedPreferencesManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual object LanguageUtils : KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    actual fun getLanguage(context: KMMContext): KMMLocale {
        val language = sharedPreferencesManager.language

        val iosLanguage = if (language.isNotEmpty()) {
            IOSLanguage.Code.from(language).toIOSLanguage()
        } else {
            IOSLanguage.Code.from(NSLocale.currentLocale.languageCode.lowercase()).toIOSLanguage()
        }

        return iosLanguage
    }

    actual fun getTransportationLanguage(context: KMMContext): TransportationLanguage {
        return if (isLangEnglish(context)) {
            TransportationLanguage.EN
        } else {
            TransportationLanguage.TC
        }
    }

    actual fun isLangEnglish(context: KMMContext): Boolean {
        val code = getLanguage(context).code
        return code == IOSLanguage.Code.EN
    }

    actual fun isLangTC(context: KMMContext): Boolean {
        return getLanguage(context).code == IOSLanguage.Code.ZH_TW
    }

    fun initLanguage() {
        val moko = getLanguage(IOSContext()).code.moko
        if (moko.isNotEmpty()) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(moko)
        } else {
            StringDesc.localeType = StringDesc.LocaleType.System
        }

    }
}