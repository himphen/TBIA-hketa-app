package hibernate.v2.ui.settings

import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.domain.ctb.CtbInteractor
import hibernate.v2.domain.gmb.GmbInteractor
import hibernate.v2.domain.kmb.KmbInteractor
import hibernate.v2.domain.lrt.LrtInteractor
import hibernate.v2.domain.mtr.MtrInteractor
import hibernate.v2.domain.nlb.NlbInteractor
import hibernate.v2.utils.IOSLanguage
import hibernate.v2.utils.LanguageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel() : KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private val kmbInteractor: KmbInteractor by inject()
    private val ctbInteractor: CtbInteractor by inject()
    private val gmbInteractor: GmbInteractor by inject()
    private val mtrInteractor: MtrInteractor by inject()
    private val lrtInteractor: LrtInteractor by inject()
    private val nlbRepository: NlbInteractor by inject()

    suspend fun resetTransportData() {
        withContext(Dispatchers.Default) {
            sharedPreferencesManager.transportDataChecksum = null
            kmbInteractor.initDatabase()
            ctbInteractor.initDatabase()
            gmbInteractor.initDatabase()
            mtrInteractor.initDatabase()
            lrtInteractor.initDatabase()
            nlbRepository.initDatabase()
        }
    }

    fun updateLang(code: IOSLanguage.Code) {
        sharedPreferencesManager.language = code.base

        LanguageUtils.initLanguage()
    }
}