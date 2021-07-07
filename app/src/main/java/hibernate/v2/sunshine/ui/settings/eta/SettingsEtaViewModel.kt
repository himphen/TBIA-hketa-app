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
            val savedKmbEtaList = etaRepository.getSavedKmbEtaList()
            convertedEtaCardList.addAll(savedKmbEtaList.map { detailsEntity ->
                Card.SettingsEtaCard(
                    entity = detailsEntity.savedEta,
                    route = detailsEntity.route.toTransportModel(),
                    stop = detailsEntity.stop.toTransportModel(),
                    position = detailsEntity.order.position,
                    type = Card.SettingsEtaCard.Type.DATA
                )
            })
            val savedNCEtaList = etaRepository.getSavedNCEtaList()
            convertedEtaCardList.addAll(savedNCEtaList.map { detailsEntity ->
                Card.SettingsEtaCard(
                    entity = detailsEntity.savedEta,
                    route = detailsEntity.route.toTransportModel(),
                    stop = detailsEntity.stop.toTransportModel(),
                    position = detailsEntity.order.position,
                    type = Card.SettingsEtaCard.Type.DATA
                )
            })

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
