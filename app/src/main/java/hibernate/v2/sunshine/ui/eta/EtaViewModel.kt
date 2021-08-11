package hibernate.v2.sunshine.ui.eta

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListMap

class EtaViewModel(
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    val savedEtaCardList = MutableLiveData<List<Card.EtaCard>>()

    fun getEtaListFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
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

        viewModelScope.launch(Dispatchers.IO) {
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
                                val temp = etaList
                                    .map { TransportEta.fromApiModel(it) }
                                    .filter { eta ->
                                        // e.g. Filter not same bound in bus terminal stops
                                        eta.bound == etaCard.route.bound
                                                && eta.seq == etaCard.stop.seq
                                    }

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)

                            }

                            result[index] = etaCard
                        }
                        Company.NWFB,
                        Company.CTB,
                        -> {

                            val apiEtaResponse = etaRepository.getNCStopEtaApi(
                                company = etaCard.route.company,
                                stopId = etaCard.stop.stopId,
                                route = etaCard.route.routeId
                            )
                            apiEtaResponse.data?.let { etaList ->
                                val temp = etaList
                                    .map { TransportEta.fromApiModel(it) }
                                    .filter { eta ->
                                        // e.g. Filter not same bound in bus terminal stops
                                        eta.bound == etaCard.route.bound
                                                && eta.seq == etaCard.stop.seq
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
                                val temp = etaRouteStop.etaList
                                    ?.map { TransportEta.fromApiModel(it) }
                                    ?.filter { eta ->
                                        // e.g. Filter not same bound in bus terminal stops
                                        eta.bound == etaCard.route.bound
                                                && eta.seq == etaCard.stop.seq
                                    } ?: emptyList()

                                etaCard.etaList.clear()
                                etaCard.etaList.addAll(temp)
                            }

                            result[index] = etaCard
                        }
                        Company.UNKNOWN -> {
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
                                    platform.etaList.forEach eta@{ etaApi ->
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
                    }
                }
            }.awaitAll()

            Logger.d("lifecycle getEtaList done")
            savedEtaCardList.postValue(result.values.toList())
        }
    }
}