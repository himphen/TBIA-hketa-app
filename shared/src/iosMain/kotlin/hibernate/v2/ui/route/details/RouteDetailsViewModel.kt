package hibernate.v2.ui.route.details

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.database.eta.SavedEtaOrderEntity
import hibernate.v2.domain.ctb.CtbInteractor
import hibernate.v2.domain.eta.EtaInteractor
import hibernate.v2.domain.gmb.GmbInteractor
import hibernate.v2.domain.kmb.KmbInteractor
import hibernate.v2.domain.lrt.LrtInteractor
import hibernate.v2.domain.mtr.MtrInteractor
import hibernate.v2.domain.nlb.NlbInteractor
import hibernate.v2.model.Card
import hibernate.v2.model.transport.RouteDetailsStop
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.eta.LRTTransportEta
import hibernate.v2.model.transport.eta.MTRTransportEta
import hibernate.v2.model.transport.eta.TransportEta
import hibernate.v2.model.transport.eta.filterCircularStop
import hibernate.v2.model.transport.route.TransportRoute
import hibernate.v2.utils.IOSContext
import hibernate.v2.utils.getEtaUpdateErrorMessage
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RouteDetailsViewModel(
    val selectedRoute: TransportRoute,
    val selectedEtaType: EtaType,
    val routeDetailsStopListUpdated: () -> Unit,
    val etaListUpdated: (List<TransportEta>) -> Unit,
    val selectedStopUpdated: () -> Unit,
    val isSavedEtaBookmarkUpdated: (Int, Int) -> Unit,
    val isRemovedEtaBookmarkUpdated: (Int) -> Unit,
    val etaUpdateError: (String) -> Unit,
) : KoinComponent {

    private val etaInteractor: EtaInteractor by inject()
    private val kmbInteractor: KmbInteractor by inject()
    private val ctbInteractor: CtbInteractor by inject()
    private val gmbInteractor: GmbInteractor by inject()
    private val mtrRepository: MtrInteractor by inject()
    private val lrtInteractor: LrtInteractor by inject()
    private val nlbRepository: NlbInteractor by inject()

    var routeDetailsStopList: List<RouteDetailsStop>? = null

    var selectedStop: TransportStop? = null
        set(value) {
            field = value
            selectedStopUpdated()
        }

    var requestedLocationUpdates = false

    private fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company,
    ): Boolean {
        return etaInteractor.hasEtaInDb(
            stopId,
            routeId,
            bound,
            serviceType,
            seq,
            company
        )
    }

    private suspend fun addEta(item: SavedEtaEntity): Int {
        return withContext(Dispatchers.Default) { etaInteractor.addEta(item) }
    }

    private suspend fun getEtaOrderList(): List<SavedEtaOrderEntity> {
        return withContext(Dispatchers.Default) { etaInteractor.getEtaOrderList() }
    }

    private suspend fun updateEtaOrderList(entityList: List<SavedEtaOrderEntity>) {
        withContext(Dispatchers.Default) { etaInteractor.updateEtaOrderList(entityList) }
    }

    @Throws(Exception::class)
    suspend fun getRouteDetailsStopList() {
        withContext(Dispatchers.Default) {
            var mRouteDetailsStopList = routeDetailsStopList
            if (mRouteDetailsStopList == null) {
                val list = getTransportStopList()
                mRouteDetailsStopList = list.map {
                    RouteDetailsStop(
                        transportStop = it,
                        savedEtaId = null
                    )
                }
            }

            val etaList = getSavedEtaListFromDb()
            val etaHashMap = etaList.associate {
                it.stopId to it.id
            }

            routeDetailsStopList = mRouteDetailsStopList.map {
                it.savedEtaId = etaHashMap[it.transportStop.stopId]
                return@map it
            }
            routeDetailsStopListUpdated()
        }
    }

    private fun getTransportStopList(): List<TransportStop> {
        val route = selectedRoute

        return when (selectedEtaType) {
            EtaType.KMB -> getKmbStopList(route)
            EtaType.NWFB,
            EtaType.CTB -> getCtbStopList(route)
            EtaType.GMB_HKI,
            EtaType.GMB_KLN,
            EtaType.GMB_NT -> getGmbStopList(route)
            EtaType.MTR -> getMtrStopList(route)
            EtaType.LRT -> getLrtStopList(route)
            EtaType.NLB -> getNlbStopList(route)
        }

    }

    private fun getSavedEtaListFromDb(): List<SavedEtaEntity> {
        return when (selectedEtaType) {
            EtaType.KMB -> etaInteractor.getSavedKmbEtaList().map { it.savedEta }
            EtaType.NWFB,
            EtaType.CTB -> etaInteractor.getSavedNCEtaList().map { it.savedEta }
            EtaType.GMB_HKI,
            EtaType.GMB_KLN,
            EtaType.GMB_NT -> etaInteractor.getSavedGmbEtaList().map { it.savedEta }
            EtaType.MTR -> etaInteractor.getSavedMTREtaList().map { it.savedEta }
            EtaType.LRT -> etaInteractor.getSavedLrtEtaList().map { it.savedEta }
            EtaType.NLB -> etaInteractor.getSavedNlbEtaList().map { it.savedEta }
        }
    }

    private fun getKmbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = kmbInteractor.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            logLifecycle("getKmbStopList done")
            allRouteList
        } catch (e: Exception) {
            logLifecycle(e, "lifecycle getKmbStopList error")
            mutableListOf()
        }

    }

    private fun getCtbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = ctbInteractor.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            logLifecycle("getCtbStopList done")
            allRouteList
        } catch (e: Exception) {
            logLifecycle(e, "lifecycle getCtbStopList error")
            mutableListOf()
        }

    }

    private fun getGmbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = gmbInteractor.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            logLifecycle("getGmbStopList done")
            allRouteList
        } catch (e: Exception) {
            logLifecycle(e, "lifecycle getGmbStopList error")
            mutableListOf()
        }

    }

    private fun getMtrStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = mtrRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            logLifecycle("getMTRStopList done")
            allRouteList
        } catch (e: Exception) {
            logLifecycle(e, "lifecycle getMTRStopList error")
            mutableListOf()
        }

    }

    private fun getLrtStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = lrtInteractor.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            logLifecycle("getLRTStopList done")
            allRouteList
        } catch (e: Exception) {
            logLifecycle(e, "lifecycle getLRTStopList error")
            mutableListOf()
        }

    }

    private fun getNlbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = nlbRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            logLifecycle("getNLBStopList done")
            allRouteList
        } catch (e: Exception) {
            logLifecycle(e, "lifecycle getNLBStopList error")
            mutableListOf()

        }
    }

    @Throws(Exception::class)
    suspend fun saveBookmark(position: Int, card: Card.RouteStopAddCard) {
        withContext(Dispatchers.Default) {
            val isExisting = hasEtaInDb(
                stopId = card.stop.stopId,
                routeId = card.route.routeId,
                bound = card.route.bound,
                serviceType = card.route.serviceType,
                seq = card.stop.seq!!,
                company = card.route.company
            )

            if (isExisting) {
                isSavedEtaBookmarkUpdated(position, 0)
                return@withContext
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

            isSavedEtaBookmarkUpdated(position, insertId)
        }
    }

    @Throws(Exception::class)
    suspend fun removeBookmark(position: Int, entityId: Int) {
        withContext(Dispatchers.Default) {
            etaInteractor.clearEta(entityId)

            isRemovedEtaBookmarkUpdated(position)
        }
    }

    suspend fun updateEtaList() {
        try {
            withContext(Dispatchers.Default) {
                logLifecycle("getEtaList")

                val selectedStop = selectedStop ?: return@withContext
                var result: List<TransportEta>? = null

                when (selectedRoute.company) {
                    Company.KMB -> {
                        val apiEtaResponse = etaInteractor.getKmbStopEtaApi(
                            stopId = selectedStop.stopId,
                            route = selectedRoute.routeId
                        )
                        apiEtaResponse.data?.let { etaList ->
                            val allSeq = etaList.mapNotNull { it.seq }.distinct()
                            val isCircular = allSeq.size > 1

                            result = etaList
                                .map { TransportEta.fromApiModel(it) }
                                .filter { it.filterCircularStop(isCircular, selectedStop) }
                        }
                    }
                    Company.NWFB,
                    Company.CTB -> {
                        val apiEtaResponse = etaInteractor.getCtbStopEtaApi(
                            company = selectedRoute.company,
                            stopId = selectedStop.stopId,
                            route = selectedRoute.routeId
                        )
                        apiEtaResponse.data?.let { etaList ->
                            val allSeq = etaList.mapNotNull { it.seq }.distinct()
                            val isCircular = allSeq.size > 1

                            result = etaList
                                .map { TransportEta.fromApiModel(it) }
                                .filter { it.filterCircularStop(isCircular, selectedStop) }
                        }
                    }
                    Company.GMB -> {
                        val apiEtaResponse = etaInteractor.getGmbStopEtaApi(
                            stopSeq = selectedStop.seq!!,
                            route = selectedRoute.routeId,
                            serviceType = selectedRoute.serviceType
                        )
                        apiEtaResponse.data?.let { etaRouteStop ->
                            val allSeq = etaRouteStop.etaList?.mapNotNull { it.seq }?.distinct()
                            val isCircular = (allSeq?.size ?: 0) > 1

                            result = etaRouteStop.etaList
                                ?.map { TransportEta.fromApiModel(it) }
                                ?.filter { it.filterCircularStop(isCircular, selectedStop) }
                                ?: emptyList()
                        }
                    }
                    Company.MTR -> {
                        val apiEtaResponse = etaInteractor.getMTRStopEtaApi(
                            stopId = selectedStop.stopId,
                            route = selectedRoute.routeId
                        )
                        val matchedIndex = selectedRoute.routeId + "-" + selectedStop.stopId
                        apiEtaResponse.data?.let { etaRouteStopMap ->
                            etaRouteStopMap.forEach { (index, etaRouteStop) ->
                                if (index != matchedIndex) return@forEach

                                result = when (selectedRoute.bound) {
                                    Bound.O -> etaRouteStop.down
                                    Bound.I -> etaRouteStop.up
                                    Bound.UNKNOWN -> null
                                }
                                    ?.map { MTRTransportEta.fromApiModel(it) }
                                    ?: emptyList()
                            }
                        }
                    }
                    Company.LRT -> {
                        val apiEtaResponse = etaInteractor.getLrtStopEtaApi(
                            stopId = selectedStop.stopId
                        )

                        apiEtaResponse.platformList?.let { platformList ->
                            val temp = mutableListOf<LRTTransportEta>()

                            platformList.forEach platform@{ platform ->
                                platform.etaList?.forEach eta@{ etaApi ->
                                    if (etaApi.routeNo != selectedRoute.routeNo) return@eta
                                    if (etaApi.destCh != selectedRoute.destTc) return@eta

                                    temp.add(
                                        LRTTransportEta.fromApiModel(
                                            etaApi,
                                            platform.platformId.toString(),
                                            apiEtaResponse.systemTime
                                        )
                                    )
                                }
                            }

                            result = temp
                        }
                    }
                    Company.NLB -> {
                        val apiEtaResponse = etaInteractor.getNlbStopEtaApi(
                            stopId = selectedStop.stopId,
                            routeId = selectedRoute.routeId
                        )
                        apiEtaResponse.data?.let { etaList ->
                            result = etaList
                                .map { TransportEta.fromApiModel(it, selectedStop.seq) }
                        }
                    }
                    Company.UNKNOWN -> {
                    }
                }

                logLifecycle("getEtaList done")
                etaListUpdated(result ?: listOf())
            }
        } catch (e: Exception) {
            etaUpdateError(getEtaUpdateErrorMessage(e, IOSContext()))
        }
    }
}
