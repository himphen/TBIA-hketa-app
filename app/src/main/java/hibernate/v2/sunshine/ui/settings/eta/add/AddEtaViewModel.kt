package hibernate.v2.sunshine.ui.settings.eta.add

import android.app.Application
import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.RouteStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddEtaViewModel(
    application: Application
) : BaseViewModel() {

    private val etaRepository = EtaRepository.getInstance(
        application.applicationContext
    )

    val allList = RouteStopListDataHolder.data

    suspend fun getEtaList(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    ) = withContext(Dispatchers.IO) {
        etaRepository.getEtaList(
            stopId,
            routeId,
            bound,
            serviceType,
            seq
        )
    }

    suspend fun addEta(item: EtaEntity) = withContext(Dispatchers.IO) { etaRepository.addEta(item) }

    suspend fun getEtaOrderList() = withContext(Dispatchers.IO) { etaRepository.getEtaOrderList() }

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaRepository.updateEtaOrderList(entityList) }

}
