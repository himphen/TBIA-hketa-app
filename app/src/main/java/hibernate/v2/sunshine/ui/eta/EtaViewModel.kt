package hibernate.v2.sunshine.ui.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
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
            val savedEtaList = etaRepository.getSavedKmbEtaList()
            val convertedEtaCardList = savedEtaList.map { etaKmbDetails ->
                Card.EtaCard(
                    route = etaKmbDetails.route.toTransportModel(),
                    stop = etaKmbDetails.stop.toTransportModelWithSeq(etaKmbDetails.savedEta.seq)
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
            val result = ConcurrentSkipListMap<Int, Card.EtaCard>()

            etaCardList.mapIndexed { index, etaCard ->
                async {
                    val apiEtaResponse = etaRepository.getStopEtaApi(
                        stopId = etaCard.stop.stopId,
                        route = etaCard.route.routeId
                    )
                    apiEtaResponse.data?.let { etaList ->
                        etaCard.etaList = etaList.filter { eta ->
                            // e.g. Filter not same bound in bus terminal stops
                            eta.dir == etaCard.route.bound && eta.seq == etaCard.stop.seq
                        }
                    }

                    result[index] = etaCard
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
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290X",
                bound = Bound.O,
                serviceType = "1",
                seq = 12,
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "403881982F9E7209",
                routeId = "296A",
                bound = Bound.O,
                serviceType = "1",
                seq = 1,
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "5527FF8CC85CF139",
                routeId = "296C",
                bound = Bound.O,
                serviceType = "1",
                seq = 1,
                brand = Brand.KMB
            )
        )
        defaultEtaEntityList.add(
            SavedEtaEntity(
                stopId = "21E3E95EAEB2048C",
                routeId = "296D",
                bound = Bound.O,
                serviceType = "1",
                seq = 1,
                brand = Brand.KMB
            )
        )
        return defaultEtaEntityList
    }
}