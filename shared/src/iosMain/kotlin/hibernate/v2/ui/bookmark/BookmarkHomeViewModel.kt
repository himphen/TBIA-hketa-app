package hibernate.v2.ui.bookmark

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.domain.eta.EtaInteractor
import hibernate.v2.model.Card
import hibernate.v2.model.transport.eta.LRTTransportEta
import hibernate.v2.model.transport.eta.MTRTransportEta
import hibernate.v2.model.transport.eta.TransportEta
import hibernate.v2.model.transport.eta.filterCircularStop
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookmarkHomeViewModel(
    private val savedEtaCardListUpdated: () -> Unit,
    private val getEtaListFromDbFailed: () -> Unit,
    private val updateEtaListFailed: () -> Unit
) : KoinComponent {

    private val etaInteractor: EtaInteractor by inject()

    var savedEtaCardList = mutableListOf<Card.EtaCard>()

    suspend fun getEtaListFromDb() {
        try {
            supervisorScope {
                val convertedEtaCardList = mutableListOf<Card.EtaCard>()
                convertedEtaCardList.addAll(
                    etaInteractor.getSavedKmbEtaList().map { it.toEtaCard() }
                )
                convertedEtaCardList.addAll(
                    etaInteractor.getSavedNCEtaList().map { it.toEtaCard() }
                )
                convertedEtaCardList.addAll(
                    etaInteractor.getSavedGmbEtaList().map { it.toEtaCard() }
                )
                convertedEtaCardList.addAll(
                    etaInteractor.getSavedMTREtaList().map { it.toEtaCard() }
                )
                convertedEtaCardList.addAll(
                    etaInteractor.getSavedLrtEtaList().map { it.toEtaCard() }
                )
                convertedEtaCardList.addAll(
                    etaInteractor.getSavedNlbEtaList().map { it.toEtaCard() }
                )

                convertedEtaCardList.sort()

                if (convertedEtaCardList.isEmpty()) {
                    savedEtaCardList = mutableListOf()
                    savedEtaCardListUpdated()
                } else {
                    savedEtaCardList = convertedEtaCardList
                    savedEtaCardListUpdated()
                }
            }
        } catch (e: Exception) {
            getEtaListFromDbFailed()
        }
    }

    suspend fun updateEtaList() {
        try {
            supervisorScope {
                logLifecycle("getEtaList")
                val etaCardList = savedEtaCardList
                if (etaCardList.isEmpty()) return@supervisorScope
                val result = mutableMapOf<Int, Card.EtaCard>()

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
                                    val allSeq =
                                        etaRouteStop.etaList?.mapNotNull { it.seq }?.distinct()
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
                                val apiEtaResponse = etaInteractor.getLrtStopEtaApi(
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
                                val apiEtaResponse = etaInteractor.getNlbStopEtaApi(
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

                logLifecycle("getEtaList done")
                savedEtaCardList = result.values.toMutableList()
                savedEtaCardListUpdated()
            }
        } catch (e: Exception) {
            updateEtaListFailed()
        }
    }
}