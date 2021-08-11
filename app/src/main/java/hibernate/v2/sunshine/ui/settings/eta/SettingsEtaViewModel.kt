package hibernate.v2.sunshine.ui.settings.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsEtaViewModel(
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    val savedEtaCardList = MutableLiveData<MutableList<Card.SettingsEtaCard>>()
    val editCard = MutableLiveData<Card.SettingsEtaCard>()

    fun getSavedEtaCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            val convertedEtaCardList = mutableListOf<Card.SettingsEtaCard>()
            convertedEtaCardList.addAll(
                etaRepository.getSavedKmbEtaList()
                    .map { it.toSettingsEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedNCEtaList()
                    .map { it.toSettingsEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedGmbEtaList()
                    .map { it.toSettingsEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedMTREtaList()
                    .map { it.toSettingsEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedLRTEtaList()
                    .map { it.toSettingsEtaCard() }
            )

            convertedEtaCardList.sort()

            savedEtaCardList.postValue(convertedEtaCardList)
        }
    }

    suspend fun clearData(item: SavedEtaEntity) = etaRepository.clearEta(item)

    suspend fun clearAllEta() = etaRepository.clearAllEta()

    suspend fun getEtaOrderList() = etaRepository.getEtaOrderList()

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        etaRepository.updateEtaOrderList(entityList)
}
