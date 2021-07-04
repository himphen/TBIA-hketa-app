package hibernate.v2.sunshine.repository

import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.kmb.KmbRouteResponse
import hibernate.v2.api.response.kmb.KmbStopResponse
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity

class EtaRepository(
    private val apiManager: ApiManager,
    private val etaDao: EtaDao,
    private val etaOrderDb: EtaOrderDao
) {

    suspend fun getSavedKmbEtaList() = etaDao.getAllKmbEtaWithOrdering()

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

    suspend fun addEta(entity: SavedEtaEntity) {
        etaDao.add(entity)
    }

    suspend fun clearEta(entity: SavedEtaEntity) {
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
        return apiManager.kmbService.getStopEta(
            stopId = stopId,
            route = route,
            serviceType = 1
        )
    }

    suspend fun getStopApi(stopId: String): KmbStopResponse {
        return apiManager.kmbService.getStop(
            stopId = stopId
        )
    }

    suspend fun getRouteApi(
        routeId: String,
        bound: Bound,
        serviceType: String
    ): KmbRouteResponse {
        return apiManager.kmbService.getRoute(
            route = routeId,
            bound = bound.value,
            serviceType = serviceType
        )
    }
}
