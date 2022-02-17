package hibernate.v2.sunshine.ui.route.mobile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.LRTTransportEta
import hibernate.v2.sunshine.model.transport.MTRTransportEta
import hibernate.v2.sunshine.model.transport.RouteDetailsStop
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop
import hibernate.v2.sunshine.model.transport.filterCircularStop
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteDetailsMobileViewModel(
    val selectedRoute: TransportRoute,
    val selectedEtaType: EtaType,
    private val etaRepository: EtaRepository,
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val routeDetailsStopList = MutableLiveData<List<RouteDetailsStop>>()
    val etaList = MutableSharedFlow<List<TransportEta>>()

    var selectedStop = MutableLiveData<TransportStop?>()

    var isSavedEtaBookmark = MutableSharedFlow<Pair<Boolean, Int>>()
    val etaUpdateError = MutableSharedFlow<Throwable>()
    val etaRequested = MutableSharedFlow<Boolean>()

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
        etaRepository.hasEtaInDb(
            stopId,
            routeId,
            bound,
            serviceType,
            seq,
            company
        )
    }

    private suspend fun addEta(item: SavedEtaEntity) =
        withContext(Dispatchers.IO) { etaRepository.addEta(item) }

    private suspend fun getEtaOrderList() =
        withContext(Dispatchers.IO) { etaRepository.getEtaOrderList() }

    private suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaRepository.updateEtaOrderList(entityList) }

    fun getRouteDetailsStopList() {
        viewModelScope.launch(Dispatchers.IO) {
            var routeDetailsStopList = routeDetailsStopList.value
            if (routeDetailsStopList == null) {
                val list = getTransportStopList()
                routeDetailsStopList =
                    list.map {
                        RouteDetailsStop(
                            transportStop = it,
                            isBookmarked = false
                        )
                    }
            }

            val etaList = getEtaListFromDb()
            val etaHashSet = etaList.map {
                it.stop.stopId
            }

            this@RouteDetailsMobileViewModel.routeDetailsStopList.postValue(
                routeDetailsStopList.map {
                    it.isBookmarked = etaHashSet.contains(it.transportStop.stopId)
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
                EtaType.CTB -> getNCStopList(route)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> getGmbStopList(route)
                EtaType.MTR -> getMTRStopList(route)
                EtaType.LRT -> getLRTStopList(route)
                EtaType.NLB -> getNLBStopList(route)
            }
        }
    }

    private suspend fun getEtaListFromDb(): List<Card.EtaCard> {
        return withContext(Dispatchers.IO) {
            return@withContext when (selectedEtaType) {
                EtaType.KMB -> etaRepository.getSavedKmbEtaList().map { it.toEtaCard() }
                EtaType.NWFB,
                EtaType.CTB -> etaRepository.getSavedNCEtaList().map { it.toEtaCard() }
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> etaRepository.getSavedGmbEtaList().map { it.toEtaCard() }
                EtaType.MTR -> etaRepository.getSavedMTREtaList().map { it.toEtaCard() }
                EtaType.LRT -> etaRepository.getSavedLRTEtaList().map { it.toEtaCard() }
                EtaType.NLB -> etaRepository.getSavedNLBEtaList().map { it.toEtaCard() }
            }
        }
    }

    private suspend fun getKmbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = kmbRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getKmbStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getKmbStopList error")
            mutableListOf()
        }
    }

    private suspend fun getNCStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = ncRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getNCStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getNCStopList error")
            mutableListOf()
        }
    }

    private suspend fun getGmbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = gmbRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getGmbStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getGmbStopList error")
            mutableListOf()
        }
    }

    private suspend fun getMTRStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = mtrRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getMTRStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getMTRStopList error")
            mutableListOf()
        }
    }

    private suspend fun getLRTStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = lrtRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getLRTStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getLRTStopList error")
            mutableListOf()
        }
    }

    private suspend fun getNLBStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = nlbRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getNLBStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getNLBStopList error")
            mutableListOf()
        }
    }

    fun saveStop(position: Int, card: Card.RouteStopAddCard) {
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
                isSavedEtaBookmark.emit(Pair(false, position))
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
            addEta(newEta)

            val currentEtaOrderList = getEtaOrderList()
            val updatedEtaOrderList = mutableListOf<EtaOrderEntity>()
            updatedEtaOrderList.add(EtaOrderEntity(id = newEta.id, position = 0))
            updatedEtaOrderList.addAll(
                currentEtaOrderList.map {
                    EtaOrderEntity(id = it.id, position = it.position + 1)
                }
            )
            updateEtaOrderList(updatedEtaOrderList)

            isSavedEtaBookmark.emit(Pair(true, position))
        }
    }

    fun updateEtaList(): Job {
        return viewModelScope.launch(Dispatchers.IO + etaExceptionHandler) {
            Logger.d("lifecycle getEtaList")

            val selectedStop = selectedStop.value ?: return@launch
            var result: List<TransportEta>? = null

            when (selectedRoute.company) {
                Company.KMB -> {
                    val apiEtaResponse = etaRepository.getKmbStopEtaApi(
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
                    val apiEtaResponse = etaRepository.getNCStopEtaApi(
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
                    val apiEtaResponse = etaRepository.getGmbStopEtaApi(
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
                    val apiEtaResponse = etaRepository.getMTRStopEtaApi(
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
                    val apiEtaResponse = etaRepository.getLRTStopEtaApi(
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
                    val apiEtaResponse = etaRepository.getNLBStopEtaApi(
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

            Logger.d("lifecycle getEtaList done")
            etaList.emit(result ?: listOf())
        }
    }
}
