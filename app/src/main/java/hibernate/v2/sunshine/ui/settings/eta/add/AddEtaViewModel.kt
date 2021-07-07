package hibernate.v2.sunshine.ui.settings.eta.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.RouteForRowAdapter
import hibernate.v2.sunshine.model.transport.TransportRouteStopList
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEtaViewModel(
    private val etaRepository: EtaRepository,
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
) : BaseViewModel() {

    val allTransportRouteList = MutableLiveData<MutableList<RouteForRowAdapter>>()

    suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
    ) = withContext(Dispatchers.IO) {
        etaRepository.hasEtaInDb(
            stopId,
            routeId,
            bound,
            serviceType,
            seq,
            company
        )
    }

    suspend fun addEta(item: SavedEtaEntity) =
        withContext(Dispatchers.IO) { etaRepository.addEta(item) }

    suspend fun getEtaOrderList() = withContext(Dispatchers.IO) { etaRepository.getEtaOrderList() }

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaRepository.updateEtaOrderList(entityList) }

    fun getTransportRouteList(etaType: AddEtaActivity.EtaType?) {
        viewModelScope.launch(Dispatchers.IO) {
            when (etaType) {
                AddEtaActivity.EtaType.KMB -> {
                    getKmbRouteList()
                }
                AddEtaActivity.EtaType.NWFB_CTB -> {
                    getNCRouteList()
                }
            }
        }
    }

    private fun getKmbRouteList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (RouteAndStopListDataHolder.hasData(AddEtaActivity.EtaType.KMB)) {
                allTransportRouteList.postValue(RouteAndStopListDataHolder.getData(AddEtaActivity.EtaType.KMB))
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
                    val routeHashId = it.routeStopEntity.routeHashId()
                    transportRouteStopHashMap[routeHashId]?.stopList?.add(
                        it.stopEntity.toTransportModelWithSeq(
                            it.routeStopEntity.seq
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

                RouteAndStopListDataHolder.setData(AddEtaActivity.EtaType.KMB, transportRouteList)

                Logger.d("lifecycle getTransportRouteList done")
                allTransportRouteList.postValue(RouteAndStopListDataHolder.getData(AddEtaActivity.EtaType.KMB))
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getTransportRouteList error")
                allTransportRouteList.postValue(mutableListOf())
            }
        }
    }

    private fun getNCRouteList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (RouteAndStopListDataHolder.hasData(AddEtaActivity.EtaType.NWFB_CTB)) {
                allTransportRouteList.postValue(RouteAndStopListDataHolder.getData(AddEtaActivity.EtaType.NWFB_CTB))
                return@launch
            }

            try {
                val allRouteList = ncRepository.getRouteListDb()
                val allRouteStopList = ncRepository.getRouteStopDetailsListDb()

                val transportRouteStopHashMap = allRouteList.associate { entity ->
                    val route = entity.toTransportModel()
                    Logger.d("route.routeHashId() " + route.routeHashId())
                    route.routeHashId() to TransportRouteStopList(
                        route = route,
                        stopList = mutableListOf()
                    )
                }

                allRouteStopList.forEach {
                    val routeHashId = it.routeStopEntity.routeHashId()
                    Logger.d("it.routeStopEntity.routeHashId() " + routeHashId)
                    transportRouteStopHashMap[routeHashId]?.stopList?.add(
                        it.stopEntity.toTransportModelWithSeq(
                            it.routeStopEntity.seq
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

                RouteAndStopListDataHolder.setData(
                    AddEtaActivity.EtaType.NWFB_CTB,
                    transportRouteList
                )

                Logger.d("lifecycle getTransportRouteList done")
                allTransportRouteList.postValue(RouteAndStopListDataHolder.getData(AddEtaActivity.EtaType.NWFB_CTB))
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getTransportRouteList error")
                allTransportRouteList.postValue(mutableListOf())
            }
        }
    }

//    fun getTransportRouteListOld() {
//        if (RouteAndStopListDataHolder.hasData()) {
//            allTransportRouteList.postValue(RouteAndStopListDataHolder.data)
//            return
//        }
//
//        viewModelScope.launch(Dispatchers.IO) {
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
//                RouteAndStopListDataHolder.data = transportRouteList
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
