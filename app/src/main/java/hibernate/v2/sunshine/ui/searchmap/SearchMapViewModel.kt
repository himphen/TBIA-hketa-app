package hibernate.v2.sunshine.ui.searchmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fonfon.kgeohash.GeoHash
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SearchMapViewModel(
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val selectedStop = MutableLiveData<SearchMapStop>()
    val stopList = MutableSharedFlow<List<SearchMapStop>>()
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
                EtaType.MTR -> mtrRepository.getRouteEtaCardList(stop)
                EtaType.LRT -> lrtRepository.getRouteEtaCardList(stop)
                EtaType.NLB -> nlbRepository.getRouteEtaCardList(stop)
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
                    EtaType.GMB_NT,
                    -> gmbRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.MTR -> mtrRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.LRT -> lrtRepository.setMapRouteListIntoMapStop(stopMapList)
                    EtaType.NLB -> nlbRepository.setMapRouteListIntoMapStop(stopMapList)
                }
            }

            stopListForBottomSheet.postValue(list.flatten())
        }
    }

    fun getStopList(list: List<GeoHash>) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredList = listOf(
                async {
                    kmbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    ncRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    gmbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
                async {
                    nlbRepository.getStopListDb(list).map {
                        SearchMapStop.fromStopEntity(it)
                    }
                },
            )
            stopList.emit(deferredList.awaitAll().flatten())
        }
    }
}
