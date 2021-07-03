package hibernate.v2.sunshine.repository

import hibernate.v2.api.model.Bound
import hibernate.v2.api.response.EtaResponse
import hibernate.v2.api.response.RouteResponse
import hibernate.v2.api.response.StopResponse
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EtaRepository(
    private val apiManager: ApiManager,
    private val etaDao: EtaDao,
    private val etaOrderDb: EtaOrderDao
) {

    suspend fun getSavedKmbEtaList() =
        withContext(Dispatchers.IO) { etaDao.getAllKmbEtaWithOrdering() }

    suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String,
        brand: Brand
    ): Boolean = etaDao.getSingleEta(
        stopId,
        routeId,
        bound,
        serviceType,
        seq,
        brand
    ) != null

    suspend fun addEta(entity: EtaEntity) {
        etaDao.add(entity)
    }

    suspend fun clearEta(entity: EtaEntity) {
        etaDao.clear(
            entity.stopId,
            entity.routeId,
            entity.bound,
            entity.serviceType,
            entity.seq
        )
    }

    suspend fun clearAllEta() {
        etaDao.clearAll()
    }

    suspend fun getEtaOrderList() = etaOrderDb.get()

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) {
        etaOrderDb.clearAll()
        etaOrderDb.add(entityList)
    }

    suspend fun getStopEtaApi(stopId: String, route: String): EtaResponse {
        return apiManager.etaService.getStopEta(
            stopId = stopId,
            route = route,
            serviceType = 1
        )
    }

    suspend fun getStopApi(stopId: String): StopResponse {
        return apiManager.etaService.getStop(
            stopId = stopId
        )
    }

    suspend fun getRouteApi(
        routeId: String,
        bound: Bound,
        serviceType: String
    ): RouteResponse {
        return apiManager.etaService.getRoute(
            route = routeId,
            bound = bound.value,
            serviceType = serviceType
        )
    }
}
