package hibernate.v2.ui.bookmark.edit

import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.database.eta.SavedEtaOrderEntity
import hibernate.v2.domain.eta.EtaInteractor
import hibernate.v2.model.Card
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookmarkEditViewModel(
    private val savedEtaCardListUpdated: () -> Unit
) : KoinComponent {

    private val etaInteractor: EtaInteractor by inject()

    var savedEtaCardList = mutableListOf<Card.SettingsEtaCard>()

    @Throws(Exception::class)
    suspend fun getSavedEtaCardList() {
        coroutineScope {
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

            savedEtaCardList = convertedEtaCardList
            savedEtaCardListUpdated()
        }
    }

    @Throws(Exception::class)
    suspend fun removeEta(item: SavedEtaEntity) {
        coroutineScope {
            etaInteractor.clearEta(item)
        }
    }

    @Throws(Exception::class)
    suspend fun clearAllEta() {
        coroutineScope {
            etaInteractor.clearAllEta()
        }
    }

    @Throws(Exception::class)
    suspend fun getEtaOrderList(): List<SavedEtaOrderEntity> {
        return coroutineScope {
            return@coroutineScope etaInteractor.getEtaOrderList()
        }
    }

    @Throws(Exception::class)
    suspend fun updateEtaOrderList(entityList: List<SavedEtaOrderEntity>) {
        coroutineScope {
            etaInteractor.updateEtaOrderList(entityList)
        }
    }
}
