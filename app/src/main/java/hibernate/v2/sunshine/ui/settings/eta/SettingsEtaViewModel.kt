package hibernate.v2.sunshine.ui.settings.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.RouteStopList
import hibernate.v2.sunshine.model.TransportRoute
import hibernate.v2.sunshine.model.TransportStop
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsEtaViewModel(
    private val kmbRepository: KmbRepository,
    private val etaRepository: EtaRepository,
) : BaseViewModel() {

    val routeHashMap = MutableLiveData<HashMap<String, TransportRoute>>()
    val stopHashMap = MutableLiveData<HashMap<String, TransportStop>>()
    val routeStopListHashMap = MutableLiveData<HashMap<String, RouteStopList>>()
    val transportRouteListReady = MutableSharedFlow<HashMap<String, TransportStop>>()
    val editCard = MutableLiveData<Card.SettingsEtaCard>()

    suspend fun getEtaList() = withContext(Dispatchers.IO) { etaRepository.getEtaList() }

    suspend fun clearData(item: EtaEntity) =
        withContext(Dispatchers.IO) {
            etaRepository.clearEta(item)
        }

    suspend fun clearAllEta() =
        withContext(Dispatchers.IO) { etaRepository.clearAllEta() }

    suspend fun getEtaOrderList() = withContext(Dispatchers.IO) { etaRepository.getEtaOrderList() }

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaRepository.updateEtaOrderList(entityList) }

    fun getTransportRouteList() {
        viewModelScope.launch {
            routeAndStopListReady.postValue(false)
            try {
                val routeResult = hashMapOf<String, TransportRoute>()

                val apiRouteList = kmbRepository.getRouteListDb()
                apiRouteList.forEach { routeEntity ->
                    val route = KmbRouteEntity.toTransportModel(routeEntity)
                    route.stopList.addAll(
                        kmbRepository.getRouteStopDetailsList(
                            routeId = route.routeId,
                            bound = route.bound,
                            serviceType = route.serviceType
                        ).map {
                            TransportStop(
                                lat = it.kmbStopEntity.lat,
                                long = it.kmbStopEntity.long,
                                nameEn = it.kmbStopEntity.nameEn,
                                nameSc = it.kmbStopEntity.nameSc,
                                nameTc = it.kmbStopEntity.nameTc,
                                stopId = it.kmbStopEntity.stopId,
                                seq = it.kmbRouteStopEntity.seq
                            )
                        }
                    )

                    routeResult[routeEntity.routeHashId()] = route
                }

                Logger.d("lifecycle getAllRouteAndStopList done")
                transportRouteListReady.emit(routeResult)
                routeAndStopListReady.postValue(true)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getAllRouteAndStopList error")
                routeAndStopListReady.postValue(false)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
