package hibernate.v2.sunshine.ui.stopmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    fun getRouteListFromStop(stopId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = kmbRepository.getRouteListFromStopId(stopId)
            routeListForBottomSheet.postValue(result)
        }
    }

    fun getStopList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (stopList.value.isNullOrEmpty()) {
                val deferredList = listOf(
                    async {
                        kmbRepository.getStopListDb().map {
                            StopMap(
                                lat = it.lat,
                                lng = it.lng,
                                nameEn = it.nameEn,
                                nameSc = it.nameEn,
                                nameTc = it.nameTc,
                                stopId = it.stopId
                            )
                        }
                    },
                    async {
                        ncRepository.getStopListDb().map {
                            StopMap(
                                lat = it.lat,
                                lng = it.lng,
                                nameEn = it.nameEn,
                                nameSc = it.nameEn,
                                nameTc = it.nameTc,
                                stopId = it.stopId
                            )
                        }
                    },
                    async {
                        gmbRepository.getStopListDb().map {
                            StopMap(
                                lat = it.lat,
                                lng = it.lng,
                                nameEn = it.nameEn,
                                nameSc = it.nameEn,
                                nameTc = it.nameTc,
                                stopId = it.stopId
                            )
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