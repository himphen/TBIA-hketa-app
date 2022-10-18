package hibernate.v2.sunshine.ui.searchmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fonfon.kgeohash.GeoHash
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.domain.GeneralInteractor
import hibernate.v2.sunshine.domain.ctb.CtbInteractor
import hibernate.v2.sunshine.domain.eta.EtaInteractor
import hibernate.v2.sunshine.domain.gmb.GmbInteractor
import hibernate.v2.sunshine.domain.kmb.KmbInteractor
import hibernate.v2.sunshine.domain.lrt.LrtInteractor
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.eta.EtaType
import hibernate.v2.sunshine.model.transport.eta.LRTTransportEta
import hibernate.v2.sunshine.model.transport.eta.MTRTransportEta
import hibernate.v2.sunshine.model.transport.eta.TransportEta
import hibernate.v2.sunshine.model.transport.eta.filterCircularStop
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListMap

class SearchMapViewModel(
    private val generalInteractor: GeneralInteractor,
    private val etaInteractor: EtaInteractor,
    private val kmbInteractor: KmbInteractor,
    private val ctbInteractor: CtbInteractor,
    private val gmbInteractor: GmbInteractor,
    private val mtrRepository: MTRRepository,
    private val lrtInteractor: LrtInteractor,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val selectedStop = MutableLiveData<SearchMapStop>()
    val stopList = MutableSharedFlow<List<SearchMapStop>>()
    val routeListForBottomSheet = MutableLiveData<List<Card.EtaCard>>()
    val stopListForBottomSheet = MutableLiveData<List<SearchMapStop>>()

    val etaUpdateError = MutableSharedFlow<Throwable>()
    val etaRequested = MutableSharedFlow<Boolean>()
    val startRefreshEtaJob = MutableSharedFlow<Unit>()
    val lastUpdatedTime = MutableLiveData<Long?>()

    val requestedLocationUpdates = MutableLiveData(false)

    private val etaExceptionHandler = CoroutineExceptionHandler { _, t ->
        run {
            viewModelScope.launch {
                etaUpdateError.emit(t)
            }
        }
    }

    fun getRouteListFromStop() {
        Logger.t("lifecycle").d("getRouteListFromStop")
        viewModelScope.launch(Dispatchers.IO) {
            val stop = selectedStop.value ?: return@launch
            val result = when (stop.etaType) {
                EtaType.KMB -> kmbInteractor.getRouteEtaCardList(stop)
                EtaType.NWFB -> ctbInteractor.getRouteEtaCardList(stop)
                EtaType.CTB -> ctbInteractor.getRouteEtaCardList(stop)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> gmbInteractor.getRouteEtaCardList(stop)
                EtaType.MTR -> mtrRepository.getRouteEtaCardList(stop)
                EtaType.LRT -> lrtInteractor.getRouteEtaCardList(stop)
                EtaType.NLB -> nlbRepository.getRouteEtaCardList(stop)
            }
            routeListForBottomSheet.postValue(result)

            startRefreshEtaJob.emit(Unit)
        }
    }

    fun getRouteListFromStopList(stopList: List<SearchMapStop>) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = stopList.groupBy({ it.etaType }, { it }).map { (etaType, stopMapList) ->
                when (etaType) {
                    EtaType.KMB -> kmbInteractor.setMapRouteListIntoMapStop(
                        stopMapList,
                        kmbInteractor.getRouteEtaCardList
                    )
                    EtaType.NWFB -> ctbInteractor.setMapRouteListIntoMapStop(
                        stopMapList,
                        ctbInteractor.getRouteEtaCardList
                    )
                    EtaType.CTB -> ctbInteractor.setMapRouteListIntoMapStop(
                        stopMapList,
                        ctbInteractor.getRouteEtaCardList
                    )
                    EtaType.GMB_HKI,
                    EtaType.GMB_KLN,
                    EtaType.GMB_NT -> gmbInteractor.setMapRouteListIntoMapStop(
                        stopMapList,
                        gmbInteractor.getRouteEtaCardList
                    )
                    EtaType.MTR -> mtrRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.LRT -> lrtInteractor.setMapRouteListIntoMapStop(
                        stopMapList,
                        lrtInteractor.getRouteEtaCardList
                    )
                    EtaType.NLB -> nlbRepository.setMapRouteListIntoMapStop(stopMapList)
                }
            }

            stopListForBottomSheet.postValue(list.flatten())
        }
    }

    fun getStopList(list: List<GeoHash>) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredList = listOf(
                async {
                    kmbInteractor.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    ctbInteractor.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    gmbInteractor.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    nlbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
            )
            val result = deferredList.awaitAll().flatten()
            stopList.emit(result)
        }
    }

    fun updateEtaList(): Job {
        return viewModelScope.launch(Dispatchers.IO + etaExceptionHandler) {
            Logger.t("lifecycle").d("getEtaList")
            val etaCardList = routeListForBottomSheet.value
            if (etaCardList == null || etaCardList.isEmpty()) return@launch

            val result = ConcurrentSkipListMap<Int, Card.EtaCard>()

            etaCardList.mapIndexed { index, etaCard ->
                async {
                    when (etaCard.route.company) {
                        Company.KMB -> {
                            val apiEtaResponse = etaInteractor.getKmbStopEtaApi(
                                stopId = etaCard.stop.stopId,
                                route = etaCard.route.routeId
                            )
                            apiEtaResponse.data?.let { etaList ->
                                val allSeq = etaList.mapNotNull { it.seq }.distinct()
                                val isCircular = allSeq.size > 1

                                val temp = etaList
                                    .map { TransportEta.fromApiModel(it) }
                                    .filter { it.filterCircularStop(isCircular, etaCard.stop) }

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)
                            }

                            result[index] = etaCard
                        }
                        Company.NWFB,
                        Company.CTB -> {
                            val apiEtaResponse = etaInteractor.getCtbStopEtaApi(
                                company = etaCard.route.company,
                                stopId = etaCard.stop.stopId,
                                route = etaCard.route.routeId
                            )
                            apiEtaResponse.data?.let { etaList ->
                                val allSeq = etaList.mapNotNull { it.seq }.distinct()
                                val isCircular = allSeq.size > 1

                                val temp = etaList
                                    .map { TransportEta.fromApiModel(it) }
                                    .filter { it.filterCircularStop(isCircular, etaCard.stop) }

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)
                            }

                            result[index] = etaCard
                        }
                        Company.GMB -> {
                            val apiEtaResponse = etaInteractor.getGmbStopEtaApi(
                                stopSeq = etaCard.stop.seq!!,
                                route = etaCard.route.routeId,
                                serviceType = etaCard.route.serviceType
                            )
                            apiEtaResponse.data?.let { etaRouteStop ->
                                val allSeq = etaRouteStop.etaList?.mapNotNull { it.seq }?.distinct()
                                val isCircular = (allSeq?.size ?: 0) > 1

                                val temp = etaRouteStop.etaList
                                    ?.map { TransportEta.fromApiModel(it) }
                                    ?.filter { it.filterCircularStop(isCircular, etaCard.stop) }
                                    ?: emptyList()

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)
                            }

                            result[index] = etaCard
                        }
                        Company.MTR -> {
                            val apiEtaResponse = etaInteractor.getMTRStopEtaApi(
                                stopId = etaCard.stop.stopId,
                                route = etaCard.route.routeId
                            )
                            val matchedIndex = etaCard.route.routeId + "-" + etaCard.stop.stopId
                            apiEtaResponse.data?.let { etaRouteStopMap ->
                                etaRouteStopMap.forEach { (index, etaRouteStop) ->
                                    if (index != matchedIndex) return@forEach

                                    val temp = when (etaCard.route.bound) {
                                        Bound.O -> etaRouteStop.down
                                        Bound.I -> etaRouteStop.up
                                        Bound.UNKNOWN -> null
                                    }
                                        ?.map { MTRTransportEta.fromApiModel(it) }
                                        ?: emptyList()

                                    etaCard.etaList.clear()
                                    etaCard.etaList.addAll(temp)

                                    etaCard.platform =
                                        (etaCard.etaList.getOrNull(0) as? MTRTransportEta)?.platform
                                            ?: "1"
                                }
                            }

                            result[index] = etaCard
                        }
                        Company.LRT -> {
                            val apiEtaResponse = etaInteractor.getLRTStopEtaApi(
                                stopId = etaCard.stop.stopId
                            )

                            apiEtaResponse.platformList?.let { platformList ->
                                val temp = mutableListOf<LRTTransportEta>()

                                platformList.forEach platform@{ platform ->
                                    platform.etaList?.forEach eta@{ etaApi ->
                                        if (etaApi.routeNo != etaCard.route.routeNo) return@eta
                                        if (etaApi.destCh != etaCard.route.destTc) return@eta

                                        temp.add(
                                            LRTTransportEta.fromApiModel(
                                                etaApi,
                                                platform.platformId.toString(),
                                                apiEtaResponse.systemTime
                                            )
                                        )
                                    }
                                }

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)

                                etaCard.platform =
                                    (etaCard.etaList.getOrNull(0) as? LRTTransportEta)?.platform
                                        ?: "1"
                            }

                            result[index] = etaCard
                        }
                        Company.NLB -> {
                            val apiEtaResponse = etaInteractor.getNLBStopEtaApi(
                                stopId = etaCard.stop.stopId,
                                routeId = etaCard.route.routeId
                            )
                            apiEtaResponse.data?.let { etaList ->
                                val temp = etaList
                                    .map { TransportEta.fromApiModel(it, etaCard.stop.seq) }

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)
                            }

                            result[index] = etaCard
                        }
                        Company.UNKNOWN -> {
                        }
                    }
                }
            }.awaitAll()

            Logger.t("lifecycle").d("getEtaList done")
            routeListForBottomSheet.postValue(result.values.toList())
        }
    }
}
