package hibernate.v2.sunshine.repository

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.request.eta.NlbRequest
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.eta.GmbEtaResponse
import hibernate.v2.api.response.eta.LrtEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import hibernate.v2.api.response.eta.NlbEtaResponse
import hibernate.v2.sunshine.api.DataServiceProvider
import hibernate.v2.sunshine.api.GmbServiceProvider
import hibernate.v2.sunshine.api.KmbServiceProvider
import hibernate.v2.sunshine.api.TransportServiceProvider
import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity

class EtaRepository(
    private val kmbServiceProvider: KmbServiceProvider,
    private val gmbServiceProvider: GmbServiceProvider,
    private val transportServiceProvider: TransportServiceProvider,
    private val etaDao: EtaDao,
    private val etaOrderDb: EtaOrderDao
) {

    suspend fun getSavedKmbEtaList() = etaDao.getAllKmbEtaWithOrdering()
    suspend fun getSavedNCEtaList() = etaDao.getAllCtbEtaWithOrdering()
    suspend fun getSavedGmbEtaList() = etaDao.getAllGmbEtaWithOrdering()
    suspend fun getSavedMTREtaList() = etaDao.getAllMtrEtaWithOrdering()
    suspend fun getSavedLRTEtaList() = etaDao.getAllLrtEtaWithOrdering()
    suspend fun getSavedNLBEtaList() = etaDao.getAllNlbEtaWithOrdering()

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

    suspend fun addEta(entity: SavedEtaEntity): Long {
        return etaDao.add(entity)
    }

    suspend fun clearEta(entityId: Long) {
        etaDao.clear(entityId)
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
        val result = ApiSafeCall {
            kmbServiceProvider.getService().getStopEta(
                stopId = stopId,
                route = route,
                serviceType = 1
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }

    suspend fun getCtbStopEtaApi(company: Company, stopId: String, route: String): EtaResponse {
        val result = ApiSafeCall {
            transportServiceProvider.getService().getCtbStopEta(
                company = company.value,
                stopId = stopId,
                route = route
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }
        return data
    }

    suspend fun getGmbStopEtaApi(
        stopSeq: Int,
        serviceType: String,
        route: String
    ): GmbEtaResponse {
        val result = ApiSafeCall {
            gmbServiceProvider.getService().getStopEta(
                stopSeq = stopSeq,
                route = route,
                serviceType = serviceType
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }

    suspend fun getMTRStopEtaApi(
        stopId: String,
        route: String
    ): MTREtaResponse {
        val result = ApiSafeCall {
            transportServiceProvider.getService().getMtrStopEta(
                stopId = stopId,
                routeId = route
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }

    suspend fun getLRTStopEtaApi(
        stopId: String
    ): LrtEtaResponse {
        val result = ApiSafeCall {
            transportServiceProvider.getService().getLrtStopEta(
                stopId = stopId
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }

    suspend fun getNLBStopEtaApi(stopId: String, routeId: String): NlbEtaResponse {
        val result = ApiSafeCall {
            transportServiceProvider.getService().getNlbStopEta(
                NlbRequest(
                    routeId = routeId,
                    stopId = stopId,
                    language = "zh"
                )
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }
}
