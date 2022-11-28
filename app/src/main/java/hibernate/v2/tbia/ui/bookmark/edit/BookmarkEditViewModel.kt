package hibernate.v2.tbia.ui.bookmark.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.database.eta.SavedEtaOrderEntity
import hibernate.v2.domain.eta.EtaInteractor
import hibernate.v2.model.Card
import hibernate.v2.tbia.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkEditViewModel(
    private val etaInteractor: EtaInteractor,
) : BaseViewModel() {

    val savedEtaCardList = MutableLiveData<MutableList<Card.SettingsEtaCard>>()
    val editCard = MutableLiveData<Card.SettingsEtaCard>()

    fun getSavedEtaCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredList = listOf(
                async {
                    etaInteractor.getSavedKmbEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaInteractor.getSavedNCEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaInteractor.getSavedGmbEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaInteractor.getSavedMTREtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaInteractor.getSavedLrtEtaList()
                        .map { it.toSettingsEtaCard() }
                },
                async {
                    etaInteractor.getSavedNlbEtaList()
                        .map { it.toSettingsEtaCard() }
                }
            )

            val convertedEtaCardList: MutableList<Card.SettingsEtaCard> =
                deferredList.awaitAll().flatten().toMutableList()

            convertedEtaCardList.sort()

            savedEtaCardList.postValue(convertedEtaCardList)
        }
    }

    suspend fun removeEta(item: SavedEtaEntity) {
        withContext(Dispatchers.IO) {
            etaInteractor.clearEta(item)
        }
    }

    suspend fun clearAllEta() {
        withContext(Dispatchers.IO) {
            etaInteractor.clearAllEta()
        }
    }

    fun getEtaOrderList(): List<SavedEtaOrderEntity> {
        return etaInteractor.getEtaOrderList()
    }

    suspend fun updateEtaOrderList(entityList: List<SavedEtaOrderEntity>) {
        withContext(Dispatchers.IO) {
            etaInteractor.updateEtaOrderList(entityList)
        }
    }
}
