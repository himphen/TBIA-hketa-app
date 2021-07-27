package hibernate.v2.sunshine.ui.stopmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.StopMap
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class StopMapViewModel(
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
) : BaseViewModel() {

    var stopList = MutableLiveData<List<StopMap>?>()
    var routeListForBottomSheet = MutableLiveData<List<TransportRoute>?>()
    var stopListForBottomSheet = MutableLiveData<List<StopMap>?>()

    fun getRouteListFromStop(etaType: EtaType, stopId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = when (etaType) {
                EtaType.KMB -> kmbRepository.getRouteListFromStopId(stopId)
                EtaType.NWFB_CTB -> ncRepository.getRouteListFromStopId(stopId)
                EtaType.GMB -> gmbRepository.getRouteListFromStopId(stopId)
            }
            routeListForBottomSheet.postValue(result)
        }
    }

    fun getRouteListFromStopList(stopMapList: List<StopMap>) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = stopMapList.groupBy({ it.etaType }, { it }).map { (etaType, stopMapList) ->
                when (etaType) {
                    EtaType.KMB -> kmbRepository.getRouteListFromStopList(stopMapList)
                    EtaType.NWFB_CTB -> ncRepository.getRouteListFromStopList(stopMapList)
                    EtaType.GMB -> gmbRepository.getRouteListFromStopList(stopMapList)
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
                            StopMap.fromStopEntity(it)
                        }
                    },
                    async {
                        ncRepository.getStopListDb().map {
                            StopMap.fromStopEntity(it)
                        }
                    },
                    async {
                        gmbRepository.getStopListDb().map {
                            StopMap.fromStopEntity(it)
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