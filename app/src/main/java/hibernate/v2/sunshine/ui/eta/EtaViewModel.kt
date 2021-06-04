package hibernate.v2.sunshine.ui.eta

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.api.request.RouteRequest
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.model.RouteEtaStop
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EtaViewModel(
    application: Application,
    private val repo: DataRepository
) : BaseViewModel() {

    val etaRepository = EtaRepository.getInstance(
        application.applicationContext
    )

    val routeEtaStopList = MutableLiveData<List<RouteEtaStop>>()
    val routeHashMap = MutableLiveData<HashMap<String, Route>>()
    val stopHashMap = MutableLiveData<HashMap<String, Stop>>()
    val routeAndStopListReady = MutableLiveData<Boolean>()

    fun getData() = etaRepository.getData()

    fun addData(item: EtaEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.addData(item)
            }
        }
    }

    fun clearData(item: EtaEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.clearData(item)
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.clearAllData()
            }
        }
    }

    fun getRouteAndStopList(list: List<EtaEntity>) {
        viewModelScope.launch {
            routeAndStopListReady.postValue(false)
            Logger.d("lifecycle getRouteAndStopList")
            try {
                val stopResult = hashMapOf<String, Stop>()
                val routeResult = hashMapOf<String, Route>()

                coroutineScope {
                    list.map { entity ->
                        entity.stopId
                    }.distinct().forEach { stopId ->
                        launch etaResult@{
                            val apiStopResponse = repo.getStop(stopId)
                            apiStopResponse.stop?.let { apiStop ->
                                stopResult[stopId] = apiStop
                            }
                        }
                    }

                    list.associate { entity ->
                        val request = RouteRequest(
                            bound = entity.bound,
                            routeId = entity.routeId,
                            serviceType = entity.serviceType
                        )

                        request.hashId() to request
                    }.forEach { (_, route) ->
                        launch etaResult@{
                            val apiStopResponse = repo.getRoute(
                                route.routeId,
                                route.bound,
                                route.serviceType
                            )
                            apiStopResponse.route?.let { apiRoute ->
                                routeResult[route.hashId()] = apiRoute
                            }
                        }
                    }
                }

                Logger.d("lifecycle getRouteAndStopList done")
                routeHashMap.postValue(routeResult)
                stopHashMap.postValue(stopResult)
                routeAndStopListReady.postValue(true)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getRouteAndStopList error")
                routeAndStopListReady.postValue(false)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    fun getEtaList(list: List<EtaEntity>) {
        viewModelScope.launch {
            Logger.d("lifecycle getEtaList")
            try {
                val routeEtaStopResult = arrayListOf<RouteEtaStop>()
                coroutineScope {
                    list.forEach { entity ->
                        launch EtaAPI@{ // this will allow us to run multiple tasks in parallel
                            val stop = stopHashMap.value?.get(entity.stopId)
                            val route =
                                routeHashMap.value?.get(entity.routeHashId())

                            if (stop == null || route == null) return@EtaAPI

                            val apiEtaResponse =
                                repo.getStopEta(entity.stopId, entity.routeId)
                            apiEtaResponse.data?.let { etaList ->
                                routeEtaStopResult.add(
                                    RouteEtaStop(
                                        route = route,
                                        stop = stop,
                                        etaList = etaList.filter { eta ->
                                            // Filter not same bound in bus terminal stops
                                            Bound.matchShortAndFull(eta.dir, entity.bound) && eta.seq == entity.seq
                                        }
                                    )
                                )
                            }
                        }
                    }
                }  // coroutineScope block will wait here until all child tasks are completed

                Logger.d("lifecycle getEtaList done")
                routeEtaStopList.postValue(routeEtaStopResult)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getEtaList error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
