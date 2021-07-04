package hibernate.v2.sunshine.ui.settings.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.RouteForRowAdapter
import hibernate.v2.sunshine.model.transport.TransportRouteStopList
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SettingsEtaViewModel(
    private val kmbRepository: KmbRepository,
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    val savedEtaCardList = MutableLiveData<MutableList<Card.SettingsEtaCard>>()
    val editCard = MutableLiveData<Card.SettingsEtaCard>()
    val addEtaListReady = MutableSharedFlow<Boolean>()

    fun getSavedEtaCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedEtaList = etaRepository.getSavedKmbEtaList()
            val convertedEtaCardList = savedEtaList.map { etaKmbDetailsEntity ->
                Card.SettingsEtaCard(
                    entity = etaKmbDetailsEntity.savedEta,
                    route = etaKmbDetailsEntity.route.toTransportModel(),
                    stop = etaKmbDetailsEntity.stop.toTransportModel(),
                    type = Card.SettingsEtaCard.Type.DATA
                )
            }.toMutableList()

            savedEtaCardList.postValue(convertedEtaCardList)
        }
    }

    suspend fun clearData(item: SavedEtaEntity) = etaRepository.clearEta(item)

    suspend fun clearAllEta() = etaRepository.clearAllEta()

    suspend fun getEtaOrderList() = etaRepository.getEtaOrderList()

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        etaRepository.updateEtaOrderList(entityList)

    fun getTransportRouteList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (RouteAndStopListDataHolder.hasData()) {
                addEtaListReady.emit(true)
                return@launch
            }

            try {
                val allRouteList = kmbRepository.getRouteListDb()
                val allRouteStopList = kmbRepository.getRouteStopDetailsListDb()

                val transportRouteStopHashMap = allRouteList.associate { entity ->
                    val route = entity.toTransportModel()
                    route.routeHashId() to TransportRouteStopList(
                        route = route,
                        stopList = mutableListOf()
                    )
                }

                allRouteStopList.forEach {
                    val routeHashId = it.kmbRouteStopEntity.routeHashId()
                    transportRouteStopHashMap[routeHashId]?.stopList?.add(
                        it.kmbStopEntity.toTransportModelWithSeq(
                            it.kmbRouteStopEntity.seq
                        )
                    )
                }

                val transportRouteStopList = transportRouteStopHashMap.values.toMutableList()
                transportRouteStopList.sortWith { o1, o2 ->
                    o1.compareTo(o2)
                }

                val transportRouteList =
                    transportRouteStopList.map { routeAndStopList ->
                        val route = routeAndStopList.route
                        val headerTitle = if (route.isSpecialRoute()) {
                            "${route.routeId} 特別線 (${route.serviceType}) - ${route.origTc} 往 ${route.destTc}"
                        } else {
                            "${route.routeId} - ${route.origTc} 往 ${route.destTc}"
                        }

                        RouteForRowAdapter(
                            headerTitle = headerTitle,
                            route = route,
                            filteredList = routeAndStopList.stopList.map { stop ->
                                Card.RouteStopAddCard(
                                    route = route,
                                    stop = stop
                                )
                            }
                        )
                    }.toMutableList()

                RouteAndStopListDataHolder.data = transportRouteList

                Logger.d("lifecycle getTransportRouteList done")
                addEtaListReady.emit(true)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getTransportRouteList error")
                addEtaListReady.emit(false)
            }
        }
    }

//    fun getTransportRouteListOld() {
//        viewModelScope.launch {
//            try {
//                val allRouteList = kmbRepository.getRouteListDb()
//                val transportRouteList = allRouteList.map { routeEntity ->
//                    val route = routeEntity.toTransportModel()
//                    val stopList =
//                        kmbRepository.getRouteStopDetailsList(
//                            routeId = route.routeId,
//                            bound = route.bound,
//                            serviceType = route.serviceType
//                        ).map {
//                            TransportStop(
//                                lat = it.kmbStopEntity.lat,
//                                lng = it.kmbStopEntity.lng,
//                                nameEn = it.kmbStopEntity.nameEn,
//                                nameSc = it.kmbStopEntity.nameSc,
//                                nameTc = it.kmbStopEntity.nameTc,
//                                stopId = it.kmbStopEntity.stopId,
//                                seq = it.kmbRouteStopEntity.seq
//                            )
//                        }.toMutableList()
//                    return@map TransportRouteStopList(
//                        route = route,
//                        stopList = stopList
//                    )
//                }.toMutableList()
//
//                transportRouteList.sortWith { o1, o2 ->
//                    o1.compareTo(o2)
//                }
//
//                Logger.d("lifecycle getTransportRouteList done")
//                allTransportRouteList.postValue(transportRouteList)
//            } catch (e: Exception) {
//                Logger.e(e, "lifecycle getTransportRouteList error")
//                allTransportRouteList.postValue(mutableListOf())
//            }
//        }
//    }
}
