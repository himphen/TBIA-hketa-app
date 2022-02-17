package hibernate.v2.sunshine.ui.eta.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class EditEtaViewModel(
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    val savedEtaCardList = MutableLiveData<MutableList<Card.SettingsEtaCard>>()
    val editCard = MutableLiveData<Card.SettingsEtaCard>()

    fun getSavedEtaCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredList = listOf(
                async {
                    etaRepository.getSavedKmbEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaRepository.getSavedNCEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaRepository.getSavedGmbEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaRepository.getSavedMTREtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaRepository.getSavedLRTEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaRepository.getSavedNLBEtaList()
                        .map { it.toSettingsEtaCard() }
                }
            )

            val convertedEtaCardList: MutableList<Card.SettingsEtaCard> =
                deferredList.awaitAll().flatten().toMutableList()

            convertedEtaCardList.sort()

            savedEtaCardList.postValue(convertedEtaCardList)
        }
    }

    suspend fun removeEta(item: SavedEtaEntity) = etaRepository.clearEta(item)

    suspend fun clearAllEta() = etaRepository.clearAllEta()

    suspend fun getEtaOrderList() = etaRepository.getEtaOrderList()

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        etaRepository.updateEtaOrderList(entityList)
}
