package hibernate.v2.sunshine.ui.searchmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class SearchMapViewModel(
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
) : BaseViewModel() {

    val selectedStop = MutableLiveData<SearchMapStop>()
    val stopList = MutableLiveData<List<SearchMapStop>>(listOf())
    val routeListForBottomSheet = MutableLiveData<List<Card.EtaCard>>(emptyList())
    val stopListForBottomSheet = MutableLiveData<List<SearchMapStop>>(emptyList())

    fun getRouteListFromStop(etaType: EtaType, stop: SearchMapStop) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = when (etaType) {
                EtaType.KMB -> kmbRepository.getRouteEtaCardList(stop)
                EtaType.NWFB -> ncRepository.getRouteEtaCardList(stop)
                EtaType.CTB -> ncRepository.getRouteEtaCardList(stop)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> gmbRepository.getRouteEtaCardList(stop)
            }
            routeListForBottomSheet.postValue(result)
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
                    EtaType.GMB_NT -> gmbRepository.setMapRouteListIntoMapStop(stopMapList)
                }
            }

            stopListForBottomSheet.postValue(list.flatten())
        }
    }

    fun getStopList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (stopList.value.isNullOrEmpty()) {
                val deferredList = listOf(
                    async {
                        kmbRepository.getStopListDb().map {
                            SearchMapStop.fromStopEntity(it)
                        }
                    },
                    async {
                        ncRepository.getStopListDb().map {
                            SearchMapStop.fromStopEntity(it)
                        }
                    },
                    async {
                        gmbRepository.getStopListDb().map {
                            SearchMapStop.fromStopEntity(it)
                        }
                    },
                )
                stopList.postValue(deferredList.awaitAll().flatten())
            } else {
                stopList.postValue(stopList.value)
            }
        }
    }
}