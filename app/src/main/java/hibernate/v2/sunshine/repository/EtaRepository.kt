package hibernate.v2.sunshine.repository

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.request.eta.NLBRequest
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.eta.GmbEtaResponse
import hibernate.v2.api.response.eta.LRTEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import hibernate.v2.api.response.eta.NLBEtaResponse
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity

class EtaRepository(
    private val apiManager: ApiManager,
    private val etaDao: EtaDao,
    private val etaOrderDb: EtaOrderDao
) {

    suspend fun getSavedKmbEtaList() = etaDao.getAllKmbEtaWithOrdering()
    suspend fun getSavedNCEtaList() = etaDao.getAllNCEtaWithOrdering()
    suspend fun getSavedGmbEtaList() = etaDao.getAllGmbEtaWithOrdering()
    suspend fun getSavedMTREtaList() = etaDao.getAllMTREtaWithOrdering()
    suspend fun getSavedLRTEtaList() = etaDao.getAllLRTEtaWithOrdering()
    suspend fun getSavedNLBEtaList() = etaDao.getAllNLBEtaWithOrdering()

    suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
    ): Boolean = etaDao.getSingleEta(
        stopId,
        routeId,
        bound,
        serviceType,
        seq,
        company
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
            ""
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

    suspend fun getKmbStopEtaApi(stopId: String, route: String): EtaResponse {
        return apiManager.kmbService.getStopEta(
            stopId = stopId,
            route = route,
            serviceType = 1
        )
    }

    suspend fun getNCStopEtaApi(company: Company, stopId: String, route: String): EtaResponse {
        return apiManager.transportService.getNCStopEta(
            company = company.value,
            stopId = stopId,
            route = route
        )
    }

    suspend fun getGmbStopEtaApi(
        stopSeq: Int,
        serviceType: String,
        route: String
    ): GmbEtaResponse {
        return apiManager.gmbService.getStopEta(
            stopSeq = stopSeq,
            route = route,
            serviceType = serviceType
        )
    }

    suspend fun getMTRStopEtaApi(
        stopId: String,
        route: String
    ): MTREtaResponse {
        return apiManager.transportService.getMTRStopEta(
            stopId = stopId,
            routeId = route
        )
    }

    suspend fun getLRTStopEtaApi(
        stopId: String
    ): LRTEtaResponse {
        return apiManager.transportService.getLRTStopEta(
            stopId = stopId
        )
    }

    suspend fun getNLBStopEtaApi(stopId: String, routeId: String): NLBEtaResponse {
        return apiManager.transportService.getNLBStopEta(
            NLBRequest(
                routeId = routeId,
                stopId = stopId,
                language = "zh"
            )
        )
    }
}
