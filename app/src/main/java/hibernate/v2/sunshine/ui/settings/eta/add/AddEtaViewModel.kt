package hibernate.v2.sunshine.ui.settings.eta.add

import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.Brand
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddEtaViewModel(
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    val allTransportRouteList = RouteAndStopListDataHolder.data

    suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String,
        brand: Brand
    ) = withContext(Dispatchers.IO) {
        etaRepository.hasEtaInDb(
            stopId,
            routeId,
            bound,
            serviceType,
            seq,
            brand
        )
    }

    suspend fun addEta(item: EtaEntity) = withContext(Dispatchers.IO) { etaRepository.addEta(item) }

    suspend fun getEtaOrderList() = withContext(Dispatchers.IO) { etaRepository.getEtaOrderList() }

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaRepository.updateEtaOrderList(entityList) }


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
