package hibernate.v2.tbia.ui.route.details.mobile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
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
import hibernate.v2.tbia.ui.base.BaseViewModel
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteDetailsMobileViewModel(
    val selectedRoute: TransportRoute,
    val selectedEtaType: EtaType,
    private val etaInteractor: EtaInteractor,
    private val kmbInteractor: KmbInteractor,
    private val ctbInteractor: CtbInteractor,
    private val gmbInteractor: GmbInteractor,
    private val mtrRepository: MtrInteractor,
    private val lrtInteractor: LrtInteractor,
    private val nlbRepository: NlbInteractor,
) : BaseViewModel() {

    val routeDetailsStopList = MutableLiveData<List<RouteDetailsStop>>()
    val etaList = MutableSharedFlow<List<TransportEta>>()

    var selectedStop = MutableLiveData<TransportStop?>()

    var isSavedEtaBookmark = MutableSharedFlow<Pair<Int, Int>>()
    var isRemovedEtaBookmark = MutableSharedFlow<Int>()
    val etaUpdateError = MutableSharedFlow<Throwable>()
    val etaRequested = MutableSharedFlow<Boolean>()
    val mapMarkerClicked = MutableSharedFlow<Int>()
    val onChangedTrafficLayerToggle = MutableSharedFlow<Boolean>()

    val requestedLocationUpdates = MutableLiveData(false)

    private val etaExceptionHandler = CoroutineExceptionHandler { _, t ->
        run {
            viewModelScope.launch {
                etaUpdateError.emit(t)
            }
        }
    }

    private suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company,
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

    private suspend fun addEta(item: SavedEtaEntity) =
        withContext(Dispatchers.IO) { return@withContext etaInteractor.addEta(item) }

    private suspend fun getEtaOrderList() =
        withContext(Dispatchers.IO) { etaInteractor.getEtaOrderList() }

    private suspend fun updateEtaOrderList(entityList: List<SavedEtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaInteractor.updateEtaOrderList(entityList) }

    fun getRouteDetailsStopList() {
        viewModelScope.launch(Dispatchers.IO) {
            var routeDetailsStopList = routeDetailsStopList.value
            if (routeDetailsStopList == null) {
                val list = getTransportStopList()
                routeDetailsStopList =
                    list.map {
                        RouteDetailsStop(
                            transportStop = it,
                            savedEtaId = null
                        )
                    }
            }

            val etaList = getSavedEtaListFromDb().filter {
                if (it.routeId == selectedRoute.routeId && it.bound == selectedRoute.bound) {
                    return@filter true
                }

                return@filter false
            }
            val etaHashMap = etaList.associate {
                it.stopId to it.id
            }

            this@RouteDetailsMobileViewModel.routeDetailsStopList.postValue(
                routeDetailsStopList.map {
                    it.savedEtaId = etaHashMap[it.transportStop.stopId]
                    return@map it
                }
            )
        }
    }

    private suspend fun getTransportStopList(): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            val route = selectedRoute

            return@withContext when (selectedEtaType) {
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
    }

    private suspend fun getSavedEtaListFromDb(): List<SavedEtaEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext when (selectedEtaType) {
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
    }

    private suspend fun getKmbStopList(route: TransportRoute): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            try {
                val allRouteList = kmbInteractor.getRouteStopComponentListDb(route)
                    .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
                logLifecycle("getKmbStopList done")
                allRouteList
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getKmbStopList error")
                mutableListOf()
            }
        }
    }

    private suspend fun getCtbStopList(route: TransportRoute): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            try {
                val allRouteList = ctbInteractor.getRouteStopComponentListDb(route)
                    .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
                logLifecycle("getCtbStopList done")
                allRouteList
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getCtbStopList error")
                mutableListOf()
            }
        }
    }

    private suspend fun getGmbStopList(route: TransportRoute): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            try {
                val allRouteList = gmbInteractor.getRouteStopComponentListDb(route)
                    .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
                logLifecycle("getGmbStopList done")
                allRouteList
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getGmbStopList error")
                mutableListOf()
            }
        }
    }

    private suspend fun getMtrStopList(route: TransportRoute): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            try {
                val allRouteList = mtrRepository.getRouteStopComponentListDb(route)
                    .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
                logLifecycle("getMTRStopList done")
                allRouteList
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getMTRStopList error")
                mutableListOf()
            }
        }
    }

    private suspend fun getLrtStopList(route: TransportRoute): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            try {
                val allRouteList = lrtInteractor.getRouteStopComponentListDb(route)
                    .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
                logLifecycle("getLRTStopList done")
                allRouteList
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getLRTStopList error")
                mutableListOf()
            }
        }
    }

    private suspend fun getNlbStopList(route: TransportRoute): List<TransportStop> {
        return withContext(Dispatchers.IO) {
            try {
                val allRouteList = nlbRepository.getRouteStopComponentListDb(route)
                    .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
                logLifecycle("getNLBStopList done")
                allRouteList
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getNLBStopList error")
                mutableListOf()
            }
        }
    }

    fun saveBookmark(position: Int, card: Card.RouteStopAddCard) {
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
                isSavedEtaBookmark.emit(Pair(position, 0))
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

            isSavedEtaBookmark.emit(Pair(position, insertId))
        }
    }

    fun removeBookmark(position: Int, entityId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            etaInteractor.clearEta(entityId)

            isRemovedEtaBookmark.emit(position)
        }
    }

    fun updateEtaList(): Job {
        return viewModelScope.launch(Dispatchers.IO + etaExceptionHandler) {
            logLifecycle("getEtaList")

            val selectedStop = selectedStop.value ?: return@launch
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
            etaList.emit(result ?: listOf())
        }
    }
}
