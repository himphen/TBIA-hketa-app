package hibernate.v2.sunshine.ui.searchmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fonfon.kgeohash.GeoHash
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.LRTTransportEta
import hibernate.v2.sunshine.model.transport.MTRTransportEta
import hibernate.v2.sunshine.model.transport.TransportEta
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListMap

class SearchMapViewModel(
    private val etaRepository: EtaRepository,
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val selectedStop = MutableLiveData<SearchMapStop>()
    val stopList = MutableSharedFlow<List<SearchMapStop>>()
    val routeListForBottomSheet = MutableLiveData<List<Card.EtaCard>>()
    val stopListForBottomSheet = MutableLiveData<List<SearchMapStop>>()

    val etaUpdateError = MutableSharedFlow<Throwable>()
    val etaRequested = MutableSharedFlow<Boolean>()
    val startRefreshEtaJob = MutableSharedFlow<Unit>()

    private val etaExceptionHandler = CoroutineExceptionHandler { _, t ->
        run {
            viewModelScope.launch {
                etaUpdateError.emit(t)
            }
        }
    }

    fun getRouteListFromStop() {
        Logger.d("lifecycle getRouteListFromStop")
        viewModelScope.launch(Dispatchers.IO) {
            val stop = selectedStop.value ?: return@launch
            val result = when (stop.etaType) {
                EtaType.KMB -> kmbRepository.getRouteEtaCardList(stop)
                EtaType.NWFB -> ncRepository.getRouteEtaCardList(stop)
                EtaType.CTB -> ncRepository.getRouteEtaCardList(stop)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> gmbRepository.getRouteEtaCardList(stop)
                EtaType.MTR -> mtrRepository.getRouteEtaCardList(stop)
                EtaType.LRT -> lrtRepository.getRouteEtaCardList(stop)
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
                    EtaType.KMB -> kmbRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.NWFB -> ncRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.CTB -> ncRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.GMB_HKI,
                    EtaType.GMB_KLN,
                    EtaType.GMB_NT,
                    -> gmbRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.MTR -> mtrRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.LRT -> lrtRepository.setMapRouteListIntoMapStop(stopMapList)
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
                    kmbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    ncRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    gmbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    nlbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
            )
            stopList.emit(deferredList.awaitAll().flatten())
        }
    }

    fun updateEtaList(): Job {
        return viewModelScope.launch(Dispatchers.IO + etaExceptionHandler) {
            Logger.d("lifecycle getEtaList")
            val etaCardList = routeListForBottomSheet.value
            if (etaCardList == null || etaCardList.isEmpty()) return@launch

            val result = ConcurrentSkipListMap<Int, Card.EtaCard>()

            etaCardList.mapIndexed { index, etaCard ->
                async {
                    when (etaCard.route.company) {
                        Company.KMB -> {
                            val apiEtaResponse = etaRepository.getKmbStopEtaApi(
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
                            val apiEtaResponse = etaRepository.getNCStopEtaApi(
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
                            val apiEtaResponse = etaRepository.getGmbStopEtaApi(
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
                            val apiEtaResponse = etaRepository.getMTRStopEtaApi(
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
                            val apiEtaResponse = etaRepository.getLRTStopEtaApi(
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
                            val apiEtaResponse = etaRepository.getNLBStopEtaApi(
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

            Logger.d("lifecycle getEtaList done")
            routeListForBottomSheet.postValue(result.values.toList())
        }
    }
}
