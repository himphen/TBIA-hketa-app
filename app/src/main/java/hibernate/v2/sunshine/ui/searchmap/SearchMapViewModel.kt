package hibernate.v2.sunshine.ui.searchmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.searchmap.Route
import hibernate.v2.sunshine.model.searchmap.Stop
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

    val selectedStop =  MutableLiveData<Stop>()
    val stopList = MutableLiveData<List<Stop>?>()
    val routeListForBottomSheet = MutableLiveData<List<Route>?>()
    val stopListForBottomSheet = MutableLiveData<List<Stop>?>()

    fun getRouteListFromStop(etaType: EtaType, stopId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = when (etaType) {
                EtaType.KMB -> kmbRepository.getMapRouteListFromStopId(stopId)
                EtaType.NWFB_CTB -> ncRepository.getMapRouteList(stopId)
                EtaType.GMB -> gmbRepository.getMapRouteListFromStopId(stopId)
            }
            routeListForBottomSheet.postValue(result)
        }
    }

    fun getRouteListFromStopList(stopList: List<Stop>) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = stopList.groupBy({ it.etaType }, { it }).map { (etaType, stopMapList) ->
                when (etaType) {
                    EtaType.KMB -> kmbRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.NWFB_CTB -> ncRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.GMB -> gmbRepository.setMapRouteListIntoMapStop(stopMapList)
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
                            Stop.fromStopEntity(it)
                        }
                    },
                    async {
                        ncRepository.getStopListDb().map {
                            Stop.fromStopEntity(it)
                        }
                    },
                    async {
                        gmbRepository.getStopListDb().map {
                            Stop.fromStopEntity(it)
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