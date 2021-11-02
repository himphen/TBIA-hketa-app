package hibernate.v2.sunshine.ui.eta.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportEta
import hibernate.v2.sunshine.model.transport.MTRTransportEta
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListMap

class EtaViewModel(
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    companion object {
        const val REFRESH_TIME = 60 * 1000L
    }

    val savedEtaCardList = MutableLiveData<List<Card.EtaCard>>()
    val etaUpdateError = MutableSharedFlow<Throwable>()

    private val etaExceptionHandler = CoroutineExceptionHandler { context, t ->
        run {
            viewModelScope.launch {
                etaUpdateError.emit(t)
            }
        }
    }

    fun getEtaListFromDb() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> run {} }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val convertedEtaCardList = mutableListOf<Card.EtaCard>()
            convertedEtaCardList.addAll(
                etaRepository.getSavedKmbEtaList().map { it.toEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedNCEtaList().map { it.toEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedGmbEtaList().map { it.toEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedMTREtaList().map { it.toEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedLRTEtaList().map { it.toEtaCard() }
            )
            convertedEtaCardList.addAll(
                etaRepository.getSavedNLBEtaList().map { it.toEtaCard() }
            )

            convertedEtaCardList.sort()

            if (convertedEtaCardList.isEmpty()) {
                savedEtaCardList.postValue(emptyList())
            } else {
                savedEtaCardList.postValue(convertedEtaCardList)
            }
        }
    }

    fun updateEtaList(etaCardList: MutableList<Card.EtaCard>?) {
        if (etaCardList == null || etaCardList.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO + etaExceptionHandler) {
            Logger.d("lifecycle getEtaList")
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
                                    .filter { eta ->
                                        if (isCircular) {
                                            // e.g. Filter bus terminal stops in circular line
                                            if (etaCard.stop.seq == 1) {
                                                eta.seq == 1
                                            } else {
                                                eta.seq != 1
                                            }
                                        } else {
                                            true
                                        }
                                    }

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
                                    .filter { eta ->
                                        if (isCircular) {
                                            // e.g. Filter bus terminal stops in circular line
                                            if (etaCard.stop.seq == 1) {
                                                eta.seq == 1
                                            } else {
                                                eta.seq != 1
                                            }
                                        } else {
                                            true
                                        }
                                    }

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
                                    ?.filter { eta ->
                                        if (isCircular) {
                                            // e.g. Filter bus terminal stops in circular line
                                            if (etaCard.stop.seq == 1) {
                                                eta.seq == 1
                                            } else {
                                                eta.seq != 1
                                            }
                                        } else {
                                            true
                                        }
                                    } ?: emptyList()

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
            savedEtaCardList.postValue(result.values.toList())
        }
    }
}