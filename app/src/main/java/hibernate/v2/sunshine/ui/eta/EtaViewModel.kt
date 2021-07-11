package hibernate.v2.sunshine.ui.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListMap

class EtaViewModel(
    private val etaRepository: EtaRepository
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

            convertedEtaCardList.sort()

            if (convertedEtaCardList.isEmpty()) {
//                savedEtaCardList.postValue(getDefaultEtaEntityList())
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
                                etaCard.etaList = etaList
                                    .map { TransportEta.fromApiModel(it) }
                                    .filter { eta ->
                                        // e.g. Filter not same bound in bus terminal stops
                                        eta.bound == etaCard.route.bound
                                                && eta.seq == etaCard.stop.seq
                                    }
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
                                etaCard.etaList = etaList
                                    .map { TransportEta.fromApiModel(it) }
                                    .filter { eta ->
                                        // e.g. Filter not same bound in bus terminal stops
                                        eta.bound == etaCard.route.bound
                                                && eta.seq == etaCard.stop.seq
                                    }
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
                                etaCard.etaList = etaRouteStop.etaList
                                    ?.map { TransportEta.fromApiModel(it) }
                                    ?.filter { eta ->
                                        // e.g. Filter not same bound in bus terminal stops
                                        eta.bound == etaCard.route.bound
                                                && eta.seq == etaCard.stop.seq
                                    } ?: emptyList()
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

    private fun getDefaultEtaEntityList(): MutableList<SavedEtaEntity> {
        val defaultEtaEntityList = mutableListOf<SavedEtaEntity>()
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290",
                bound = Bound.O,
                serviceType = "1",
                seq = 2,
                company = Company.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290X",
                bound = Bound.O,
                serviceType = "1",
                seq = 12,
                company = Company.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "403881982F9E7209",
                routeId = "296A",
                bound = Bound.O,
                serviceType = "1",
                seq = 1,
                company = Company.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "5527FF8CC85CF139",
                routeId = "296C",
                bound = Bound.O,
                serviceType = "1",
                seq = 1,
                company = Company.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "21E3E95EAEB2048C",
                routeId = "296D",
                bound = Bound.O,
                serviceType = "1",
                seq = 1,
                company = Company.KMB
            )
        )
        return defaultEtaEntityList
    }
}