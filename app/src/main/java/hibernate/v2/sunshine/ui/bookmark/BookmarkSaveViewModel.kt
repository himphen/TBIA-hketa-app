package hibernate.v2.sunshine.ui.bookmark

import androidx.lifecycle.viewModelScope
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.database.eta.SavedEtaOrderEntity
import hibernate.v2.domain.eta.EtaInteractor
import hibernate.v2.model.Card
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkSaveViewModel(
    private val etaInteractor: EtaInteractor,
) : BaseViewModel() {

    var isAddEtaSuccessful = MutableSharedFlow<Boolean>()

    private suspend fun addEta(item: SavedEtaEntity) =
        withContext(Dispatchers.IO) { etaInteractor.addEta(item) }

    private suspend fun getEtaOrderList() =
        withContext(Dispatchers.IO) { etaInteractor.getEtaOrderList() }

    private suspend fun updateEtaOrderList(entityList: List<SavedEtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaInteractor.updateEtaOrderList(entityList) }

    private suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
    ) = withContext(Dispatchers.IO) {
        etaInteractor.hasEtaInDb(
            stopId,
            routeId,
            bound,
            serviceType,
            seq,
            company
        )
    }

    fun saveStop(card: Card.RouteStopAddCard) {
        viewModelScope.launch(Dispatchers.IO) {
            val isExisting = hasEtaInDb(
                stopId = card.stop.stopId,
                routeId = card.route.routeId,
                bound = card.route.bound,
                serviceType = card.route.serviceType,
                seq = card.stop.seq!!,
                company = card.route.company
            )

            if (isExisting) {
                isAddEtaSuccessful.emit(false)
                return@launch
            }

            val newEta = SavedEtaEntity(
                stopId = card.stop.stopId,
                routeId = card.route.routeId,
                bound = card.route.bound,
                serviceType = card.route.serviceType,
                seq = card.stop.seq!!,
                company = card.route.company
            )
            val insertId = addEta(newEta)

            val currentEtaOrderList = getEtaOrderList()
            val updatedEtaOrderList = mutableListOf<SavedEtaOrderEntity>()
            updatedEtaOrderList.add(SavedEtaOrderEntity(id = newEta.id, position = 0))
            updatedEtaOrderList.addAll(
                currentEtaOrderList.map {
                    SavedEtaOrderEntity(id = it.id, position = it.position + 1)
                }
            )
            updateEtaOrderList(updatedEtaOrderList)

            isAddEtaSuccessful.emit(true)
        }
    }
}
