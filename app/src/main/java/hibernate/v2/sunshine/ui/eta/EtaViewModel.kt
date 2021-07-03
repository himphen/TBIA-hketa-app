package hibernate.v2.sunshine.ui.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class EtaViewModel(
    private val etaRepository: EtaRepository
) : BaseViewModel() {

    val savedEtaCardList = MutableLiveData<List<Card.EtaCard>>().apply {
        value = listOf()
    }

    fun getEtaListFromDb() {
        viewModelScope.launch {
            val savedEtaList = etaRepository.getSavedKmbEtaList()
            val convertedEtaCardList = savedEtaList.map { etaKmbDetailsEntity ->
                Card.EtaCard(
                    route = etaKmbDetailsEntity.kmbRouteEntity.toTransportModel(),
                    stop = etaKmbDetailsEntity.kmbStopEntity.toTransportModel()
                )
            }

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
            try {
                val list = arrayOfNulls<Card.EtaCard>(etaCardList.size)

                etaCardList.mapIndexed { index, etaCard ->
                    async {
                        val apiEtaResponse = etaRepository.getStopEtaApi(
                            stopId = etaCard.stop.stopId,
                            route = etaCard.route.routeId
                        )
                        apiEtaResponse.data?.let { etaList ->
                            etaCard.etaList = etaList.filter { eta ->
                                // Filter not same bound in bus terminal stops
                                eta.dir == etaCard.route.bound && eta.seq == etaCard.stop.seq
                            }.toMutableList()
                        }

                        list[index] = etaCard
                        Logger.d("lifecycle getStopEta done: " + etaCard.stop.stopId + "-" + etaCard.route.routeId)
                    }
                }.awaitAll()

                Logger.d("lifecycle getEtaList done")
                savedEtaCardList.postValue(list.filterNotNull())
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getEtaList error")
            }
        }
    }

    private fun getDefaultEtaEntityList(): MutableList<EtaEntity> {
        val defaultEtaEntityList = mutableListOf<EtaEntity>()
        defaultEtaEntityList.add(
            EtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290",
                bound = Bound.OUTBOUND,
                serviceType = "1",
                seq = "2",
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            EtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290X",
                bound = Bound.OUTBOUND,
                serviceType = "1",
                seq = "12",
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            EtaEntity(
                stopId = "403881982F9E7209",
                routeId = "296A",
                bound = Bound.OUTBOUND,
                serviceType = "1",
                seq = "1",
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            EtaEntity(
                stopId = "5527FF8CC85CF139",
                routeId = "296C",
                bound = Bound.OUTBOUND,
                serviceType = "1",
                seq = "1",
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            EtaEntity(
                stopId = "21E3E95EAEB2048C",
                routeId = "296D",
                bound = Bound.OUTBOUND,
                serviceType = "1",
                seq = "1",
                brand = Brand.KMB
            )
        )
        return defaultEtaEntityList
    }
}
